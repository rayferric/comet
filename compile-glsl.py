import os

def compile(name):
	os.system('glslangValidator -G -o src/main/resources/shaders/{}.vert.spv data/{}.vert'.format(name, name))
	os.system('glslangValidator -G -o src/main/resources/shaders/{}.frag.spv data/{}.frag'.format(name, name))

print('Compiling using glslangValidator...\n')

compile('basic')

input('\nPress ENTER to continue...')