import random
import time

random.seed(time.time())
a = abs(random.randint(0, 20))
b = abs(random.randint(0, 20))

task = "Enter the multiplication of integers"
expression = str(a) + " * " + str(b)
answer = str(a * b)