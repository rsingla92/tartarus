
#ifndef SERIALPORT_H_
#define SERIALPORT_H_
#include <stdio.h>
#include <string.h>

int writeSerialData(FILE *rs232, unsigned char data);
int readSerialData(FILE *rs232, unsigned char *data);
int readSerialDataWait(FILE *rs232, unsigned char *data);
FILE* initSerialPort(const char* name);
void sendRequestData(FILE *rs232);

#endif /* SERIALPORT_H_ */
