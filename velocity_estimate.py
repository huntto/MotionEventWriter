import sys
import matplotlib.pyplot as plt
import numpy as np


def solve_least_squares_deg2(y, t):
    Sy = 0.0
    St = 0.0
    St2 = 0.0
    St3 = 0.0
    St4 = 0.0
    Syt = 0.0
    Syt2 = 0.0
    for i in range(y.shape[0]):
        ti = t[i]
        yi = y[i]
        t2 = ti * ti
        t3 = t2 * ti
        t4 = t3 * ti
        Sy += yi
        St += ti
        St2 += t2
        St3 += t3
        St4 += t4
        Syt += yi * ti
        Syt2 += yi * t2
    n = len(y)
    denominator = (St * St2 / n - St3) * (St * St2 / n - St3) - (St2 * St2 / n - St4) * (St * St / n - St2)
    if denominator == 0:
        return None
    b2 = ((Sy * St / n - Syt) * (St * St2 / n - St3) - (Sy * St2 / n - Syt2) * (St * St / n - St2)) / denominator
    b1 = ((Sy * St2 / n - Syt2) * (St * St2 / n - St3) - (Sy * St / n - Syt) * (St2 * St2 / n - St4)) / denominator
    b0 = Sy / n - b1 * St / n - b2 * St2 / n
    return np.array([b0, b1, b2])


def velocity_estimate(x, y, t):
    n = len(x)
    res_x = np.array([])
    res_y = np.array([])
    for i in range(n):
        s = 0
        if i > 20:
            s = i - 20
        e = i + 1
        sx = x[s:e].copy()
        sy = y[s:e].copy()
        st = t[s:e].copy()
        for j in range(len(st)):
            st[j] = st[j] - t[i]
        param_y = solve_least_squares_deg2(sy, st)
        param_x = solve_least_squares_deg2(sx, st)
        if param_y is not None:
            res_y = np.append(res_y, param_y[1])
        else:
            res_y = np.append(res_y, 0)
        if param_x is not None:
            res_x = np.append(res_x, param_x[1])
        else:
            res_x = np.append(res_x, 0)
    return res_x, res_y


def load_points_from_file(filepath):
    data = np.loadtxt(filepath, delimiter=',').reshape(-1, 3)
    return data


filepath = "pointer_data_20250817_100135.txt"

if len(sys.argv) > 1:
    filepath = sys.argv[1]

points = load_points_from_file(filepath)
x = points[:, 0]
y = points[:, 1]
t = points[:, 2]

res = velocity_estimate(x, y, t)
vx = res[0]
vy = res[1]

plt.figure(1)
plt.scatter(x, y, c='r', marker='o')
plt.xlabel("x")
plt.ylabel("y")

plt.figure(2)
plt.scatter(t, vx, c='b', marker='o')
plt.xlabel("t")
plt.ylabel("velocity x")

plt.figure(3)
plt.scatter(t, vy, c='g', marker='o')
plt.xlabel("t")
plt.ylabel("velocity y")

plt.show()
