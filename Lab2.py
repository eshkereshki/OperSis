"""
Лабораторная работа №2 по ОС
Умножение матриц N x N с использованием n процессов.
Размер матрицы и число процессов задаются через аргументы командной строки.
"""

import sys          # для чтения аргументов командной строки
import time
import random
import csv
from multiprocessing import Pool


def multiply_row(args):
    """
    Вычисляет одну строку результирующей матрицы C = A * B.
    Выполняется в отдельном процессе.
    """
    row_index, row_a, matrix_b = args
    n = len(matrix_b)
    result_row = [0] * n
    for j in range(n):
        total = 0
        for k in range(n):
            total += row_a[k] * matrix_b[k][j]
        result_row[j] = total
    return row_index, result_row


def multiply_matrices_with_processes(matrix_a, matrix_b, num_processes):
    """
    Умножает матрицы с использованием num_processes процессов.
    """
    n = len(matrix_a)
    tasks = [(i, matrix_a[i], matrix_b) for i in range(n)]
    with Pool(processes=num_processes) as pool:
        results = pool.map(multiply_row, tasks)
    result_matrix = [None] * n
    for idx, row in results:
        result_matrix[idx] = row
    return result_matrix


def generate_random_matrix(n, seed=42):
    """
    Генерирует случайную матрицу n x n с целыми числами от 0 до 9.
    """
    random.seed(seed)
    return [[random.randint(0, 9) for _ in range(n)] for _ in range(n)]


def main():
    """
    Главная функция: читает аргументы, запускает тесты, записывает результаты.
    """
    # Проверяем количество аргументов
    if len(sys.argv) != 3:
        print("Использование: python matrix.py <N> <max_processes>")
        print("Пример: python matrix.py 400 4")
        sys.exit(1)

    try:
        N = int(sys.argv[1])             # размер матрицы
        max_processes = int(sys.argv[2]) # максимальное число процессов
    except ValueError:
        print("Ошибка: аргументы должны быть целыми числами!")
        sys.exit(1)

    if N <= 0 or max_processes <= 0:
        print("Ошибка: N и max_processes должны быть положительными!")
        sys.exit(1)

    print(f"Умножение матриц {N}x{N} с использованием процессов")

    # Генерация матриц — НЕ ВХОДИТ В ЗАМЕР!
    print(f"Генерация матриц {N}x{N}...")
    matrix_a = generate_random_matrix(N)
    matrix_b = generate_random_matrix(N)
    print("Матрицы сгенерированы.")

    # Подготовка CSV-файла
    csv_filename = "results_processes.csv"
    with open(csv_filename, mode='w', newline='', encoding='utf-8') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(["Processes", "AverageTimeMillis"])

        # Тестируем от 1 до max_processes процессов
        for p in range(1, max_processes + 1):
            times = []
            print(f"\nТестирование с {p} процессами...")

            for trial in range(3):
                start_time = time.perf_counter()
                multiply_matrices_with_processes(matrix_a, matrix_b, p)
                end_time = time.perf_counter()
                elapsed_ms = (end_time - start_time) * 1000
                times.append(elapsed_ms)
                print(f"  Замер {trial + 1}: {elapsed_ms:.2f} мс")

            avg_time = sum(times) / len(times)
            print(f"  Среднее: {avg_time:.2f} мс")
            writer.writerow([p, round(avg_time, 2)])

    print(f"\nРезультаты сохранены в файл: {csv_filename}")


if __name__ == "__main__":
    main()
