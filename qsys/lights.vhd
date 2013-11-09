LIBRARY ieee;
USE ieee.std_logic_1164.all; 
USE ieee.std_logic_arith.all; 
USE ieee.std_logic_unsigned.all; 

ENTITY lights IS
   PORT (
      SW : IN STD_LOGIC_VECTOR(7 DOWNTO 0);
      KEY : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		GPIO_1 : INOUT STD_LOGIC_VECTOR(35 downto 0);
      CLOCK_50 : IN STD_LOGIC;
      LEDG : OUT STD_LOGIC_VECTOR(7 DOWNTO 0);
		LEDR : OUT STD_LOGIC_VECTOR(17 downto 0);
      DRAM_CLK, DRAM_CKE : OUT STD_LOGIC;
      DRAM_ADDR : OUT STD_LOGIC_VECTOR(11 DOWNTO 0);
      DRAM_BA_0, DRAM_BA_1 : BUFFER STD_LOGIC;
      DRAM_CS_N, DRAM_CAS_N, DRAM_RAS_N, DRAM_WE_N : OUT STD_LOGIC;
      DRAM_DQ : INOUT STD_LOGIC_VECTOR(15 DOWNTO 0);
      DRAM_UDQM, DRAM_LDQM : BUFFER STD_LOGIC;
      LCD_DATA : inout STD_LOGIC_VECTOR(7 downto 0);
	   LCD_ON, LCD_BLON, LCD_EN, LCD_RS, LCD_RW : out STD_LOGIC;
		VGA_R : out STD_LOGIC_VECTOR(9 downto 0);
		VGA_G : out STD_LOGIC_VECTOR(9 downto 0);
		VGA_B : out STD_LOGIC_VECTOR(9 downto 0);
		VGA_CLK : out STD_LOGIC; 
		VGA_BLANK : out STD_LOGIC;
		VGA_HS : out STD_LOGIC;
		VGA_VS : out STD_LOGIC; 
		VGA_SYNC : out STD_LOGIC;
		SRAM_DQ : INOUT STD_LOGIC_VECTOR(15 downto 0);
		SRAM_ADDR : OUT STD_LOGIC_VECTOR(17 downto 0);
		SRAM_LB_N : OUT STD_LOGIC;
		SRAM_UB_N : OUT STD_LOGIC;
		SRAM_CE_N : OUT STD_LOGIC;
		SRAM_OE_N : OUT STD_LOGIC;
		SRAM_WE_N : OUT STD_LOGIC;
		SD_CMD, SD_DAT, SD_DAT3 : INOUT STD_LOGIC;
		SD_CLK : OUT STD_LOGIC;
		I2C_SDAT : inout STD_LOGIC;
		I2C_SCLK : OUT STD_LOGIC;
		AUD_ADCDAT : IN STD_LOGIC;
		AUD_ADCLRCK : IN STD_LOGIC;
		AUD_BCLK : IN STD_LOGIC;
		AUD_DACDAT : OUT STD_LOGIC;
		AUD_DACLRCK : IN STD_LOGIC;
		AUD_XCK : OUT STD_LOGIC;
		CLOCK_27 : IN STD_LOGIC;
		TD_RESET : OUT STD_LOGIC;
		UART_RXD : IN STD_LOGIC;
		UART_TXD : OUT STD_LOGIC);
		
   END lights;



ARCHITECTURE Structure OF lights IS

   COMPONENT nios_system PORT (
      clk_clk : IN STD_LOGIC;
      reset_reset_n : IN STD_LOGIC;
      sdram_clk_clk : OUT STD_LOGIC;
      leds_export : OUT STD_LOGIC_VECTOR(7 DOWNTO 0);
      switches_export : IN STD_LOGIC_VECTOR(7 DOWNTO 0);
      sdram_wire_addr : OUT STD_LOGIC_VECTOR(11 DOWNTO 0);
      sdram_wire_ba : BUFFER STD_LOGIC_VECTOR(1 DOWNTO 0); 
      sdram_wire_cas_n : OUT STD_LOGIC;
      sdram_wire_cke : OUT STD_LOGIC;
      sdram_wire_cs_n : OUT STD_LOGIC;
      sdram_wire_dq : INOUT STD_LOGIC_VECTOR(15 DOWNTO 0);
      sdram_wire_dqm : BUFFER STD_LOGIC_VECTOR(1 DOWNTO 0);
      sdram_wire_ras_n : OUT STD_LOGIC;
      sdram_wire_we_n : OUT STD_LOGIC;
		lcd_data_DATA : inout STD_LOGIC_VECTOR(7 downto 0);
		lcd_data_ON : out STD_LOGIC;
		lcd_data_BLON : out STD_LOGIC;
		lcd_data_EN : out STD_LOGIC;
		lcd_data_RS : out STD_LOGIC;
		lcd_data_RW : out STD_LOGIC;
		buttons_export : IN STD_LOGIC_VECTOR(3 downto 0);
		vga_controller_CLK : out STD_LOGIC;
		vga_controller_HS : out STD_LOGIC;
		vga_controller_VS : out STD_LOGIC; 
		vga_controller_BLANK : out STD_LOGIC;
		vga_controller_SYNC : out STD_LOGIC;
		vga_controller_R : out STD_LOGIC_VECTOR(9 downto 0);
		vga_controller_G : out STD_LOGIC_VECTOR(9 downto 0);
		vga_controller_B : out STD_LOGIC_VECTOR(9 downto 0);
		pixel_buffer_DQ : inout STD_LOGIC_VECTOR(15 downto 0);
		pixel_buffer_ADDR : out STD_LOGIC_VECTOR(17 downto 0);
		pixel_buffer_LB_N : out STD_LOGIC;
		pixel_buffer_UB_N : out STD_LOGIC;
		pixel_buffer_CE_N : out STD_LOGIC;
		pixel_buffer_OE_N : out STD_LOGIC;
		pixel_buffer_WE_N : out STD_LOGIC;
		sdcard_b_SD_cmd : inout STD_LOGIC;
		sdcard_b_SD_dat : inout STD_LOGIC;
		sdcard_b_SD_dat3 : inout STD_LOGIC;
		sdcard_o_SD_clock : OUT STD_LOGIC;
		controller_out_export : out STD_LOGIC_VECTOR(7 downto 0);
		controller_in_export : in STD_LOGIC_VECTOR(7 downto 0);
		av_config_SDAT : inout STD_LOGIC;
		av_config_SCLK : OUT STD_LOGIC;
		audio_clock_secondary_out_clk : OUT STD_LOGIC;
		audio_clock_secondary_in_clk : IN STD_LOGIC;
		audio_ADCDAT : IN STD_LOGIC;
		audio_ADCLRCK : IN STD_LOGIC;
		audio_BCLK :  IN STD_LOGIC;    
		audio_DACDAT : OUT STD_LOGIC;    
		audio_DACLRCK :  IN STD_LOGIC;
		rs232_0_external_interface_RXD : IN STD_LOGIC;
		rs232_0_external_interface_TXD : OUT STD_LOGIC
		);
 
   END COMPONENT;

   SIGNAL DQM : STD_LOGIC_VECTOR(1 DOWNTO 0);
   SIGNAL BA : STD_LOGIC_VECTOR(1 DOWNTO 0);
	SIGNAL CONTROLLER_OUT : STD_LOGIC_VECTOR(7 downto 0);

   BEGIN
		
      DRAM_BA_0 <= BA(0);
      DRAM_BA_1 <= BA(1);
      DRAM_UDQM <= DQM(1);
      DRAM_LDQM <= DQM(0);
		--LEDR(1 downto 0) <= GPIO_1(4) & GPIO_1(2);
		--GPIO_1(4) <= SW(0);
		--GPIO_1(2) <= SW(1);
		
		GPIO_1(2) <= CONTROLLER_OUT(0);
		GPIO_1(4) <= CONTROLLER_OUT(1);
		
      NiosII: nios_system PORT MAP (
          clk_clk => CLOCK_50,
          reset_reset_n => KEY(0) OR KEY(1),
          sdram_clk_clk => DRAM_CLK,
          leds_export => LEDG,
          switches_export => SW,
          sdram_wire_addr => DRAM_ADDR,
          sdram_wire_ba => BA,
          sdram_wire_cas_n => DRAM_CAS_N,
          sdram_wire_cke => DRAM_CKE,
          sdram_wire_cs_n => DRAM_CS_N,
          sdram_wire_dq => DRAM_DQ,
          sdram_wire_dqm => DQM,
          sdram_wire_ras_n => DRAM_RAS_N,
          sdram_wire_we_n => DRAM_WE_N,
			 lcd_data_DATA => LCD_DATA,
			 lcd_data_ON => LCD_ON,
			 lcd_data_EN => LCD_EN,
			 lcd_data_RS => LCD_RS,
			 lcd_data_RW => LCD_RW,
			 lcd_data_BLON => LCD_BLON,
			 buttons_export => KEY,
			 vga_controller_CLK => VGA_CLK,
			 vga_controller_HS => VGA_HS,
			 vga_controller_VS => VGA_VS, 
			 vga_controller_BLANK => VGA_BLANK,
			 vga_controller_SYNC => VGA_SYNC,
			 vga_controller_R => VGA_R,
			 vga_controller_G => VGA_G,
			 vga_controller_B => VGA_B,
			 pixel_buffer_DQ => SRAM_DQ,
			 pixel_buffer_ADDR => SRAM_ADDR,
			 pixel_buffer_LB_N => SRAM_LB_N,
			 pixel_buffer_UB_N => SRAM_UB_N,
			 pixel_buffer_CE_N => SRAM_CE_N,
			 pixel_buffer_OE_N => SRAM_OE_N, 
			 pixel_buffer_WE_N => SRAM_WE_N,
			 sdcard_b_SD_cmd => SD_CMD,
			 sdcard_b_SD_dat => SD_DAT,		
			 sdcard_b_SD_dat3 => SD_DAT3,
			 sdcard_o_SD_clock => SD_CLK,
			 controller_out_export => CONTROLLER_OUT,
			 controller_in_export => GPIO_1(13 downto 6),
			 av_config_SDAT => I2C_SDAT,
			 av_config_SCLK => I2C_SCLK,
		    audio_clock_secondary_out_clk => AUD_XCK,
			 audio_clock_secondary_in_clk => CLOCK_27,
			 audio_ADCDAT => AUD_ADCDAT,
			 audio_ADCLRCK => AUD_ADCLRCK,
			 audio_BCLK => AUD_BCLK,
			 audio_DACDAT => AUD_DACDAT,
			 audio_DACLRCK =>  AUD_DACLRCK,
			 rs232_0_external_interface_RXD => UART_RXD,
			 rs232_0_external_interface_TXD => UART_TXD);

   END Structure;