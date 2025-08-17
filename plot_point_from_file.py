import sys
import matplotlib.pyplot as plt
import numpy as np

def load_points_from_file(filepath):
    data = np.loadtxt(filepath, delimiter=',').reshape(-1, 3)
    return data

filepath = "pointer_data_20250817_100135.txt"

if len(sys.argv) > 1:
    filepath = sys.argv[1]

points = load_points_from_file(filepath)
x = points[:, 0]
y = points[:, 1]

plt.scatter(x, y, c='r', marker='o')
plt.xlabel("x")
plt.ylabel("y")
plt.show()