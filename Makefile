
##COMPILER_NVIM_STOP

.PHONY: all clean test run

MAIN_CLASS = $(shell xmllint --xpath "string(//mainClass)" pom.xml 2>/dev/null)
ifeq ($(MAIN_CLASS),)
MAIN_CLASS = $(shell grep -oPm1 "(?<=<mainClass>)[^<]+" pom.xml)
endif
ifeq ($(MAIN_CLASS),)
$(warning No <mainClass> found! Defaulting.)
MAIN_CLASS = com.jless.chess.App
endif

print-main-class:
	@echo $(MAIN_CLASS)

MVN_RUN := mvn -q exec:java -Dexec.mainClass=$(MAIN_CLASS)

all:
	mvn -q clean
	mvn -q package
	$(MVN_RUN)

clean:
	mvn -q clean

test:
	mvn -q test

run:
	$(MVN_RUN)



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
