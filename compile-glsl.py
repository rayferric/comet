import os

def compile(name):
	os.system('glslangValidator -G -o src/main/resources/shaders/{}.vert.spv data/shaders/{}.vert'.format(name, name))
	os.system('glslangValidator -G -o src/main/resources/shaders/{}.frag.spv data/shaders/{}.frag'.format(name, name))
	print()

print('Compiling using glslangValidator...\n')

compile('basic')

input('Press ENTER to continue...')