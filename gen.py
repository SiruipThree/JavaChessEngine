import random

def rand_pair():
    a = random.randint(0, 7)
    b = random.randint(0, 7)
    c = (a + random.randint(-1, 1)) % 8
    d = (b + random.randint(-1, 1)) % 8
    return chr(a + ord('a')) + str(b + 1), chr(c + ord('a')) + str(d + 1)

for _ in range(10000):
    p1, p2 = rand_pair()
    print(f"{p1} {p2}")

print("quit")
