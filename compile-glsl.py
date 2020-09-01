import os

def compile(name, vert=True, frag=True):
	if vert:
		os.system('glslangValidator -G -o src/main/resources/shaders/{}.vert.spv data/shaders/{}.vert'.format(name, name))
	if frag:
		os.system('glslangValidator -G -o src/main/resources/shaders/{}.frag.spv data/shaders/{}.frag'.format(name, name))
	print()

print('Compiling using glslangValidator...\n')

compile('basic')
compile('depth', frag=False)
compile('font')
compile('gltf')
compile('graph')
compile('sprite')

input('Press ENTER to continue...')