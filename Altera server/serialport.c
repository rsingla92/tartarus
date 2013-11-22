/*
 * Implementation wrapper functions for serialport.c
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "serialport.h"


void startSerialReadInterrupt(alt_up_rs232_dev *rs232)
{
	alt_up_rs232_enable_read_interrupt(rs232);
}

void stopSerialReadInterrupt(alt_up_rs232_dev *rs232)
{
	alt_up_rs232_disable_read_interrupt(rs232);
}

int checkSerialParity(alt_u32 data_reg)
{
	return alt_up_rs232_check_parity(data_reg);
}

unsigned getSerialUsedSpace(alt_up_rs232_dev *rs232)
{
	return alt_up_rs232_get_used_space_in_read_FIFO(rs232);
}

unsigned getSerialFreeSpace(alt_up_rs232_dev *rs232)
{
	return alt_up_rs232_get_available_space_in_write_FIFO(rs232);
}

int writeSerialData(alt_up_rs232_dev *rs232, alt_u8 data)
{
	return alt_up_rs232_write_data(rs232, data);
}

int readSerialData(alt_up_rs232_dev *rs232, alt_u8 *data, alt_u8 *parity_error)
{
	return alt_up_rs232_read_data(rs232, data, parity_error);
}

int readSerialFD(alt_fd* fd, char* ptr, int len)
{
	return alt_up_rs232_read_fd (fd, ptr, len);
}

int writeSerialFD(alt_fd* fd, const char* ptr, int len)
{
	return alt_up_rs232_write_fd (fd, ptr, len);
}

alt_up_rs232_dev* initSerialPort(const char* name) {
	return alt_up_rs232_open_dev(name);
}

