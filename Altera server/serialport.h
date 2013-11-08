
#ifndef SERIALPORT_H_
#define SERIALPORT_H_
#include "altera_up_avalon_rs232.h"

void startSerialReadInterrupt(alt_up_rs232_dev *rs232);

void stopSerialReadInterrupt (alt_up_rs232_dev *rs232);

int checkSerialParity(alt_u32 data_reg);

unsigned getSerialUsedSpace(alt_up_rs232_dev *rs232);

unsigned getSerialFreeSpace(alt_up_rs232_dev *rs232);

int writeSerialData(alt_up_rs232_dev *rs232, alt_u8 data);

int readSerialData(alt_up_rs232_dev *rs232, alt_u8 *data, alt_u8 *parity_error);

int readSerialFD(alt_fd* fd, char* ptr, int len);

int writeSerialFD(alt_fd* fd, const char* ptr, int len);

alt_up_rs232_dev* initSerialPort(const char* name);

#endif /* SERIALPORT_H_ */
