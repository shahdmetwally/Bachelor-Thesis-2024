import requests
import json
import csv

def get_chat_response(prompt):
    api_key = '***REMOVED***'
    endpoint = 'https://api.openai.com/v1/chat/completions'

    headers = {
        'Content-Type': 'application/json',
        'Authorization': f'Bearer {api_key}'
    }

    data = {
        'model': 'gpt-4',
        'messages': [
            {
                'role': 'user',
                'content': prompt
            }
        ]
    }

    with requests.Session() as session:
        response = session.post(endpoint, headers=headers, json=data)

    if response.status_code == 200:
        response_data = response.json()
        chat_result = response_data['choices'][0]['message']['content']
        return chat_result
    else:
        print(f"Failed to get response: {response.text}")
        return None

def send_prompts(prompts):
    with open('/Data/chatgpt/responses.csv', mode='w', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)
        writer.writerow(['Prompt', 'Response']) #column titles
        for prompt in prompts:
            response = get_chat_response(prompt)
            print(f"Prompt: {prompt}\nResponse: {response}\n{'-'*40}")
            writer.writerow([prompt, response])

def read_file(file_path):
    with open(file_path, 'r', encoding='latin1') as file:
        return file.read()

def process_files(file_paths, prompt_styles):
    prompts = []
    for file_path in file_paths:
        file_content = read_file(file_path)
        for prompt_style in prompt_styles:
            for _ in range(10):
                prompt = f"{prompt_style}\n{file_content}"
                prompts.append(prompt)
    print(prompts)
    send_prompts(prompts)

def main():
    # Lists of file paths for Singleton and Observer design patterns
    singleton_files = [
        'DesignPatterns/Singleton/Assert.java',
        'DesignPatterns/Singleton/Clipboard.java',
        'DesignPatterns/Singleton/CodeGenerator.java',
        'DesignPatterns/Singleton/Debug.java',
        'DesignPatterns/Singleton/HTMLComponentFactory.java',
        'DesignPatterns/Singleton/IconKit.java',
        'DesignPatterns/Singleton/IconManager.java',
        'DesignPatterns/Singleton/NullWritable.java',
        'DesignPatterns/Singleton/PackageListFilter.java',
        'DesignPatterns/Singleton/ReloaderSingleton.java',
        'DesignPatterns/Singleton/UndoStack.java',
        'DesignPatterns/Singleton/Version.java'
    ]
    observer_files = [
        'DesignPatterns/Observer/ASTModel.java',
        'DesignPatterns/Observer/ConnectionFigure.java',
        'DesignPatterns/Observer/Drawing.java',
        'DesignPatterns/Observer/Editor.java',
        'DesignPatterns/Observer/FileActionListener.java',
        'DesignPatterns/Observer/SelectionModel.java',
        'DesignPatterns/Observer/TestListener.java',
        'DesignPatterns/Observer/TestResult.java',
        'DesignPatterns/Observer/TestSuite.java',
        'DesignPatterns/Observer/TextModel.java',
        'DesignPatterns/Observer/ViewerModel.java' 
    ]
    singleton_prompts = [
        # Context Manager Prompt Style
        "Within the scope of software engineering, please consider the following design pattern principles: Singleton is when there is only one instance of a class. Observer consists of one parent object that changes state, other objects are notified. Now identify the design pattern used in the following code snippet: ",
        # Persona Prompt Style
        "From now on act as a Software Architect. Please identify the design pattern in the following code sample: "
    ]
    observer_prompts = [
        # Context Manager Prompt Style
        "Within the scope of software engineering, please consider the following design pattern principles: Singleton is when there is only one instance of a class. Observer consists of one parent object that changes state, other objects are notified. Now identify the design pattern used in the following code snippet: ",
        # Persona Prompt Style
        "From now on act as a Software Architect. Please identify the design pattern in the following code sample: "
    ]

    # Sending Singleton prompts
    print("Singleton Design Pattern Responses:")
    process_files(singleton_files, singleton_prompts)

    # Sending Observer prompts
    print("\nObserver Design Pattern Responses:")
    process_files(observer_files, observer_prompts)

if __name__ == '__main__':
    main()