import http.server
import threading
import requests
import json
import time
import csv

token = None

def setup():
    resp = requests.post('https://github.com/login/device/code', headers={
            'accept': 'application/json',
            'editor-version': 'Neovim/0.6.1',
            'editor-plugin-version': 'copilot.vim/1.16.0',
            'content-type': 'application/json',
            'user-agent': 'GithubCopilot/1.155.0',
            'accept-encoding': 'gzip,deflate,br'
        }, data='{"client_id":"Iv1.b507a08c87ecfe98","scope":"read:user"}')

    # Parse the response json, isolating the device_code, user_code, and verification_uri
    resp_json = resp.json()
    device_code = resp_json.get('device_code')
    user_code = resp_json.get('user_code')
    verification_uri = resp_json.get('verification_uri')

    # Print the user code and verification uri
    print(f'Please visit {verification_uri} and enter code {user_code} to authenticate.')

    while True:
        time.sleep(5)
        resp = requests.post('https://github.com/login/oauth/access_token', headers={
            'accept': 'application/json',
            'editor-version': 'Neovim/0.6.1',
            'editor-plugin-version': 'copilot.vim/1.16.0',
            'content-type': 'application/json',
            'user-agent': 'GithubCopilot/1.155.0',
            'accept-encoding': 'gzip,deflate,br'
            }, data=f'{{"client_id":"Iv1.b507a08c87ecfe98","device_code":"{device_code}","grant_type":"urn:ietf:params:oauth:grant-type:device_code"}}')

        # Parse the response json, isolating the access_token
        resp_json = resp.json()
        access_token = resp_json.get('access_token')

        if access_token:
            break

    # Save the access token to a file
    with open('.copilot_token', 'w') as f:
        f.write(access_token)

    print('Authentication success!')


def get_token():
    global token
    # Check if the .copilot_token file exists
    while True:
        try:
            with open('.copilot_token', 'r') as f:
                access_token = f.read()
                break
        except FileNotFoundError:
            setup()

    # Get a session with the access token
    resp = requests.get('https://api.github.com/copilot_internal/v2/token', headers={
        'authorization': f'token {access_token}',
        'editor-version': 'Neovim/0.6.1',
        'editor-plugin-version': 'copilot.vim/1.16.0',
        'user-agent': 'GithubCopilot/1.155.0'
    })

    # Parse the response json, isolating the token
    resp_json = resp.json()
    token = resp_json.get('token')


def token_thread():
    global token
    while True:
        get_token()
        time.sleep(25 * 60)
    
def copilot(prompt, language='python'):
    global token
    # If the token is None, get a new one
    if token is None:
        get_token()

    try:
        resp = requests.post('https://copilot-proxy.githubusercontent.com/v1/engines/copilot-codex/completions', headers={'authorization': f'Bearer {token}'}, json={
            'prompt': prompt,
            'suffix': '',
            'max_tokens': 1000,
            'temperature': 0,
            'top_p': 1,
            'n': 1,
            'stop': ['\n'],
            'nwo': 'github/copilot.vim',
            'stream': True,
            'extra': {
                'language': language
            }
        })
    except requests.exceptions.ConnectionError:
        return ''

    results = []
    

    # Parse the response text, splitting it by newlines
    resp_text = resp.text.split('\n')
    for line in resp_text:
        # If the line contains a completion, print it
        if line.startswith('data: {'):
            # Parse the completion from the line as json
            json_completion = json.loads(line[6:])
            completion = json_completion.get('choices')[0].get('text')
            if completion:
                results.append(completion)
    results_string = ''.join(results)
    return results_string

def read_file(file_path):
    with open(file_path, 'r', encoding='latin1') as file:
        return file.read()

def process_files(file_paths, prompt):
    prompts = []
    for file_path in file_paths:
        file_content = read_file(file_path)
        for _ in range(1):
            prompt_with_content = f"{prompt}\n{file_content}"
            prompts.append(prompt_with_content)
    return prompts

def send_prompts(prompts):
    with open('responses.csv', mode='a', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)
        # Write column titles only if the file is empty
        if file.tell() == 0:
            writer.writerow(['Prompt', 'Response']) 
        for prompt in prompts:
            response = copilot(prompt)
            print(f"Prompt: {prompt}\nResponse: {response}\n{'-'*40}")
            writer.writerow([prompt, response])

def main():
    singleton_files = [
        'DesignPatterns/Singleton/Iconkit.java',
        # Add other Singleton file paths here
    ]
    observer_files = [
        'DesignPatterns/Observer/ASTModel.java',
        # Add other Observer file paths here
    ]
    prompt = "Please identify the design pattern in the following code snippet:"

    # Process Singleton files
    singleton_prompts = process_files(singleton_files, prompt)

    # Process Observer files
    observer_prompts = process_files(observer_files, prompt)

    # Sending Singleton prompts
    print("Singleton Design Pattern Responses:")
    send_prompts(singleton_prompts)

    # Sending Observer prompts
    print("Observer Design Pattern Responses:")
    send_prompts(observer_prompts)

if __name__ == '__main__':
    main()
