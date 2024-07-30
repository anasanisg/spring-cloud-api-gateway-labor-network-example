
MVN := mvn
OUTPUT_PATH := build/
SRC_PATH_WALLTOOLS := walltools-service/
SRC_PATH_DISCOVERY := discovery-server/
SRC_PATH_GATEWAY := gateway/

.PHONY: all build copy dockerize

all: build copy

build:
	@echo "Building Walltools - Service ..."
	@cd ${SRC_PATH_WALLTOOLS} && $(MVN) clean package -DskipTests -Pprod
	@cd ${SRC_PATH_DISCOVERY} && $(MVN) clean package -DskipTests
	@cd ${SRC_PATH_GATEWAY} && $(MVN) clean package -DskipTests -Pprod

copy:
	@echo "Copy to build directory ..."
	mkdir -p ${OUTPUT_PATH}
	cp ${SRC_PATH_WALLTOOLS}/target/*.jar ${OUTPUT_PATH}
	cp ${SRC_PATH_DISCOVERY}/target/*.jar ${OUTPUT_PATH}	
	cp ${SRC_PATH_GATEWAY}/target/*.jar ${OUTPUT_PATH}

dockerize:
	@echo "Building Docker Images"
	@cd ${SRC_PATH_WALLTOOLS} && docker build -t walltools .
	@cd ${SRC_PATH_DISCOVERY} && docker build -t discovery .
	@cd ${SRC_PATH_GATEWAY} && docker build -t gateway .

dev:
	@cd ${SRC_PATH_WALLTOOLS} && mvn clean spring-boot:run -Pdev

