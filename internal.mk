# internal.mk — NOT scanned by compiler.nvim

clean:
	mvn -q clean

build:
	mvn -q package

build_and_run:
	mvn -q package
	mvn -q exec:java -Dexec.mainClass=$(MAIN_CLASS)

run:
	mvn -q exec:java -Dexec.mainClass=$(MAIN_CLASS)

