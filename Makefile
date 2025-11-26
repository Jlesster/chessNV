.PHONY: all clean test maven_build_and_run maven_build maven_run

MAIN_CLASS = $(shell xmllint --xpath "string(//mainClass)" pom.xml 2>/dev/null)
ifeq ($(MAIN_CLASS),)
MAIN_CLASS = $(shell grep -oPm1 "(?<=<mainClass>)[^<]+" pom.xml)
endif
ifeq ($(MAIN_CLASS),)
$(warning No <mainClass> found! Defaulting.)
MAIN_CLASS = com.jless.chess.App
endif


maven_build_and_run:
	mvn -q package
	$(MVN_RUN)

maven_build:
	mvn -q package

maven_run:
	$(MVN_RUN)
all:
	maven_build

clean:
	mvn -q clean

test:
	mvn -q test

MVN_RUN := mvn -q exec:java -Dexec.mainClass=$(MAIN_CLASS)

# ================================
# Auto-detect the main class
# ================================
# Try with xmllint (best and safest)


# ================================
# Required default targets
# ================================


# ================================
# Compiler.nvim integration targets
# ================================
