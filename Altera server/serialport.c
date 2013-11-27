/*
 * Implementation wrapper functions for serialport.c
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "serialport.h"

int writeSerialData(FILE *rs232, unsigned char data)
{
	return fputc(data, rs232);
}

int readSerialData(FILE *rs232, unsigned char *data)
{
	int dat = fgetc(rs232);
	*data = dat;
	return dat;
}

int readSerialDataWait(FILE *rs232, unsigned char *data)
{
	int c;
	while ((c = fgetc(rs232)) == EOF) {
		printf("Eof Loop!\n");
	}
	*data = c;
	return c;
}

void sendRequestData(FILE *rs232)
{
	fputc(0, rs232);
}

FILE* initSerialPort(const char* name) {
	return fopen(name, "r+");
}

