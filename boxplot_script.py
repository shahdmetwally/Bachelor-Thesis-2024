import matplotlib.pyplot as plt

# Data for the GPT box plot
data = {
    'Precision': [0.9495, 0.7460],
    'Accuracy': [0.7460, 0.8458],
    'Recall': [0.7757, 0.8458],
    'F1-Score': [0.8545, 0.9165]
}

# Creating the box plot
fig, ax = plt.subplots()
ax.boxplot(data.values())
ax.set_xticklabels(data.keys())

# Add title and labels
plt.title('ChatGPT Performance Metrics')
plt.ylabel('Scores')

# Show the plot
plt.show()