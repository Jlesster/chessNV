.PHONY: all clean test maven_build_and_run maven_build maven_run

# ================================
# Auto-detect the main class
# ================================
# Try with xmllint (best and safest)
MAIN_CLASS := $(shell xmllint --xpath "string(//mainClass)" pom.xml 2>/dev/null)

# If xmllint failed, fall back to grep
ifeq ($(MAIN_CLASS),)
MAIN_CLASS := $(shell grep -oPm1 "(?<=<mainClass>)[^<]+" pom.xml)
endif

# Safety fallback (avoid empty java commands)
ifeq ($(MAIN_CLASS),)
$(warning No <mainClass> found in pom.xml! Set MAIN_CLASS manually.)
MAIN_CLASS := com.jless.chess.App
endif


# ================================
# Required default targets
# ================================
all: maven_build

clean:
	mvn -q clean

test:
	mvn -q test


# ================================
# Compiler.nvim integration targets
# ================================
maven_build_and_run:
	mvn -q package
	mvn -q exec:java -Dexec.mainClass=$(MAIN_CLASS)

maven_build:
	mvn -q package

maven_run:
 mvn -q exec:java -Dexec.mainClass=$(MAIN_CLASS)
