import csv

def count_occurrences_and_rows(file_path, search_word, column_name):
    occurrences = 0
    total_rows = 0

    with open(file_path, mode='r', encoding='latin-1') as file:
        csv_reader = csv.reader(file)
        
        headers = next(csv_reader)
        try:
            response_index = headers.index(column_name)
        except ValueError:
            print(f"Column '{column_name}' not found in the CSV headers.")
            exit()
        
        for row in csv_reader:
            # Skip rows that do not have enough columns
            if len(row) <= response_index:
                print(f"Row {total_rows + 1} is missing data for '{column_name}' column. Skipping...")
                continue
            
            total_rows += 1
            if search_word.lower() in row[response_index].lower():
                occurrences += 1

    return occurrences, total_rows

def calculate_metrics(tp, fp, fn, total_runs):
    precision = tp / (tp + fp) if (tp + fp) else 0
    recall = tp / (tp + fn) if (tp + fn) else 0
    accuracy = (tp + (total_runs - (tp + fp + fn))) / total_runs if total_runs else 0
    f1_score = 2 * (precision * recall) / (precision + recall) if (precision + recall) else 0
    
    return precision, accuracy, recall, f1_score

# Paths to CSV files
observer_file = 'Data/chatgpt/responses_observer.csv'
singleton_file = 'Data/chatgpt/responses_singleton.csv'

# Calculating occurrences and rows (Observer)
observer_occurrences, observer_rows = count_occurrences_and_rows(observer_file, "observer", "Response")
singleton_occurrences_observer, singleton_rows = count_occurrences_and_rows(singleton_file, "observer", "Response")

# Calculating occurrences and rows (Singleton)
observer_occurrences_singleton, observer_rows = count_occurrences_and_rows(observer_file, "singleton", "Response")
singleton_occurrences, singleton_rows = count_occurrences_and_rows(singleton_file, "singleton", "Response")

# Calculate metrics for "observer"
tp_observer = observer_occurrences
fp_observer = singleton_occurrences_observer
fn_observer = observer_rows - observer_occurrences
total_runs_observer = observer_rows + singleton_rows 

# Observer Quality Metrics
precision, accuracy, recall, f1_score = calculate_metrics(tp_observer, fp_observer, fn_observer, total_runs_observer)
print(f"Observer - Precision: {precision}, Accuracy: {accuracy}, Recall: {recall}, F1 Score: {f1_score}")

# Calculate metrics for "singleton"
tp_singleton = singleton_occurrences
fp_singleton = observer_occurrences_singleton
fn_singleton = singleton_rows - singleton_occurrences
total_runs_singleton = observer_rows + singleton_rows

# Singleton Quality Metrics
precision, accuracy, recall, f1_score = calculate_metrics(tp_singleton, fp_singleton, fn_singleton, total_runs_singleton)
print(f"Singleton - Precision: {precision}, Accuracy: {accuracy}, Recall: {recall}, F1 Score: {f1_score}")