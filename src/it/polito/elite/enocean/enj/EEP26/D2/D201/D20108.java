/*
 * EnJ - EnOcean Java API
 * 
 * Copyright 2014 Andrea Biasi, Dario Bonino 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package it.polito.elite.enocean.enj.EEP26.D2.D201;

import it.polito.elite.enocean.enj.EEP26.EEPIdentifier;
import it.polito.elite.enocean.enj.EEP26.EEPRegistry;
import it.polito.elite.enocean.enj.EEP26.attributes.EEPAttribute;
import it.polito.elite.enocean.enj.EEP26.attributes.EEPDefaultState;
import it.polito.elite.enocean.enj.EEP26.attributes.EEPEnergyMeasurement;
import it.polito.elite.enocean.enj.EEP26.attributes.EEPLocalControl;
import it.polito.elite.enocean.enj.EEP26.attributes.EEPOverCurrentShutdown;
import it.polito.elite.enocean.enj.EEP26.attributes.EEPOverCurrentShutdownReset;
import it.polito.elite.enocean.enj.EEP26.attributes.EEPPowerFailure;
import it.polito.elite.enocean.enj.EEP26.attributes.EEPPowerMeasurement;
import it.polito.elite.enocean.enj.EEP26.attributes.EEPSwitching;
import it.polito.elite.enocean.enj.EEP26.attributes.EEPUserInterfaceMode;
import it.polito.elite.enocean.enj.EEP26.packet.EEP26Telegram;
import it.polito.elite.enocean.enj.EEP26.packet.EEP26TelegramType;
import it.polito.elite.enocean.enj.EEP26.packet.VLDTelegram;
import it.polito.elite.enocean.enj.communication.EnJConnection;

import java.io.Serializable;

/**
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 * 
 */
public class D20108 extends D201 implements Serializable
{
	/**
	 * class version number for serialization / de-serialization
	 */
	private static final long serialVersionUID = 1L;

	// the type definition
	public static final byte type = (byte) 0x08;

	// the ON state / command
	public static boolean ON = true;

	// the ON command byte
	public static byte ON_BYTE = (byte) 0x64;

	// the OFF state / command
	public static boolean OFF = false;

	// the OFF command byte
	public static byte OFF_BYTE = (byte) 0x00;

	// the byte identifier for all output channels
	public static byte ALL_OUTPUT_CHANNEL = 0x1E;

	// the "data" fields accessible through this eep (and updated upon network
	// data reception)

	// register the type in the EEPProfile even if no instance of this class is
	// created.
	static
	{
		EEPRegistry.getInstance().addProfile(
				new EEPIdentifier(D201.rorg, D201.func, D20108.type),
				D20108.class);
	}

	/**
	 * Builds a new EEPProfile instance of type D2.01.08 as specified in the
	 * EEP2.6 specification.
	 */
	public D20108()
	{
		super("2.6");

		// add the supported functions
		this.addChannelAttribute(1, new EEPSwitching());
		this.addChannelAttribute(1, new EEPEnergyMeasurement());
		this.addChannelAttribute(1, new EEPPowerMeasurement());
		this.addChannelAttribute(1, new EEPLocalControl());
		this.addChannelAttribute(1, new EEPUserInterfaceMode());
		this.addChannelAttribute(1, new EEPDefaultState());
		this.addChannelAttribute(1, new EEPOverCurrentShutdown());
		this.addChannelAttribute(1, new EEPOverCurrentShutdownReset());
		this.addChannelAttribute(1, new EEPPowerFailure());
	}

	// execution commands
	public void actuatorSetOuput(EnJConnection connection,
			byte[] deviceAddress, boolean command)
	{
		// exec the command by using the D201 general purpose implementation
		super.actuatorSetOutput(connection, deviceAddress,
				D201DimMode.SWITCH_TO_NEW_OUTPUT_VALUE.getCode(),
				D20108.ALL_OUTPUT_CHANNEL, command ? D20108.ON_BYTE
						: D20108.OFF_BYTE);
	}

	public void actuatorSetOuput(EnJConnection connection,
			byte[] deviceAddress, int dimValue, D201DimMode dimMode)
	{
		// check limits
		if (dimValue < 0)
			dimValue = 0;
		else if (dimValue > 100)
			dimValue = 100;

		super.actuatorSetOutput(connection, deviceAddress, dimMode.getCode(),
				D20108.ALL_OUTPUT_CHANNEL, (byte) dimValue);

	}

	/**
	 * Updates the configuration of the physical actuator having this EEP
	 * profile with values from the given set of attributes. Attributes not
	 * being part of the acceptable configuration parameters will be simply
	 * ignored.
	 * 
	 * @param connection
	 *            The {@link EnJConnection} object enabling physical layer
	 *            communication
	 * @param deviceAddress
	 *            The physical layer address of the device
	 * @param attributes
	 *            The configuration attributes to set
	 */
	public void actuatorSetLocal(EnJConnection connection,
			byte[] deviceAddress, int channelId,
			EEPAttribute<? extends Object>[] attributes, D201DimTime dimTime1,
			D201DimTime dimTime2, D201DimTime dimTime3)
	{
		// the over current shutdown settings (enabled / disabled), disabled by
		// default
		byte overCurrentShutDown = 0x00;

		// the reset behavior in overcurrent shutdown cases
		byte resetOverCurrentShutDown = 0x00;

		// the local control enabling flag
		byte localControl = 0x00;

		// the user interface mode (either day or night)
		byte userInterfaceIndication = 0x00;

		// the power failure flag
		byte powerFailure = 0x00;

		// the default state to set when the actuator is powered
		byte defaultState = 0x00;

		// extract the attributes
		// TODO: find a better way to perform such operations, if possible
		for (EEPAttribute<? extends Object> attribute : attributes)
		{
			if (attribute instanceof EEPLocalControl)
				localControl = attribute.byteValue()[0];
			else if (attribute instanceof EEPOverCurrentShutdown)
				overCurrentShutDown = attribute.byteValue()[0];
			else if (attribute instanceof EEPOverCurrentShutdownReset)
				resetOverCurrentShutDown = attribute.byteValue()[0];
			else if (attribute instanceof EEPUserInterfaceMode)
				userInterfaceIndication = attribute.byteValue()[0];
			else if (attribute instanceof EEPPowerFailure)
				powerFailure = attribute.byteValue()[0];
			else if (attribute instanceof EEPDefaultState)
				defaultState = attribute.byteValue()[0];
		}

		// call the superclass method for setting the device configuration
		super.actuatorSetLocal(connection, deviceAddress, (byte) channelId,
				localControl, overCurrentShutDown, resetOverCurrentShutDown,
				userInterfaceIndication, powerFailure, defaultState, dimTime1,
				dimTime2, dimTime3);
	}

	public void actuatorSetMeasurement(EnJConnection connection,
			byte[] deviceAddress, boolean autoReportMesurement,
			boolean signalResetMeasurement, boolean powerMode, int channelId,
			int measurementDeltaToBeReported, D201UnitOfMeasure unitOfMeasure,
			int maximumTimeBetweenActuatorMessages,
			int minimumTimeBetweenActuatorMessages)
	{
		if ((maximumTimeBetweenActuatorMessages >= 0)
				&& (minimumTimeBetweenActuatorMessages >= 0))
		{
			byte reportMeasurementAsByte = autoReportMesurement ? (byte) 0x01
					: (byte) 0x00;
			byte signalResetMeasurementAsByte = signalResetMeasurement ? (byte) 0x01
					: (byte) 0x00;
			byte powerModeAsByte = powerMode ? (byte) 0x01 : (byte) 0x00;

			// get the measurement delta LSB, and with all 0 apart from the last
			// 4 bits
			byte measurementDeltaLSB = (byte) ((measurementDeltaToBeReported) & (0x000F));

			// get the measurement delta MSB, shift right by 8 bits and set at 0
			// the 8 leading bits
			byte measurementDeltaMSB = (byte) ((measurementDeltaToBeReported >> 8) & (0x00FF));
			byte maximumTimeAsByte = (byte) Math.min(
					(maximumTimeBetweenActuatorMessages / 10), 255);
			byte minimumTimeAsByte = (byte) Math.min(
					minimumTimeBetweenActuatorMessages, 255);

			// call the superclass method
			super.actuatorSetMeasurement(connection, deviceAddress,
					reportMeasurementAsByte, signalResetMeasurementAsByte,
					powerModeAsByte, (byte) channelId, measurementDeltaLSB,
					unitOfMeasure.getCode(), measurementDeltaMSB,
					maximumTimeAsByte, minimumTimeAsByte);
		}
		else
			throw new NumberFormatException(
					"Only positive numbers allowed for time values");
	}

	/**
	 * Asks for the current power or energy measurement on a given channel Id of
	 * a given EnOcean actuator
	 * 
	 * @param connection
	 * @param deviceAddress
	 * @param powerMode
	 * @param channelId
	 */
	public void actuatorMeasurementQuery(EnJConnection connection,
			byte[] deviceAddress, boolean powerMode, int channelId)
	{
		// get the measurement mode as a byte value
		byte powerModeAsByte = powerMode ? (byte) 0x01 : (byte) 0x00;

		// call the superclass method
		super.actuatorMeasurementQuery(connection, deviceAddress,
				powerModeAsByte, (byte) channelId);
	}

	@Override
	public EEPIdentifier getEEPIdentifier()
	{
		return new EEPIdentifier(D201.rorg, D201.func, D20108.type);
	}

	@Override
	public boolean handleProfileUpdate(EEP26Telegram telegram)
	{
		boolean success = false;
		// handle the telegram, as first cast it at the right type (or fail)
		if (telegram.getTelegramType() == EEP26TelegramType.VLD)
		{
			// cast the telegram to the right type
			VLDTelegram profileUpdate = (VLDTelegram) telegram;

			// check the telegram type:actuator status response (CMD_ID = 0x04),
			// Actuator Measurement Response (CMD_ID = 0x07)
			byte dataPayload[] = profileUpdate.getPayload();

			// get the command id
			byte commandId = (byte) (dataPayload[0] & (byte) 0x0F);

			if (commandId == (byte) 0x04)
			{
				// parse actuator status response
				D201ActuatorStatusResponse response = this
						.parseActuatorStatusResponse(dataPayload);

				// TODO: update attributes

				// TODO: check how to notify attribute listeners
			}
			else if (commandId == (byte) 0x07)
			{
				// parse the actuator measurement response
				D201ActuatorMeasurementResponse response = this
						.parseActuatorMeasurementResponse(dataPayload);

				// TODO: update attributes

				// get the channel to attribute to update, can either be Energy
				// or Power measurement, detect it depending on the unit of
				// measure.
				D201UnitOfMeasure uom = response.getUnit();
				
				if(uom.isEnergy())
					//handle energy attribute update
					this.updateEnergyAttribute(response);
				else if(uom.isPower())
					//handle power attribute update
					this.updatePowerAttribute(response);

				// TODO: check how to notify attribute listeners
			}
		}
		return success;
	}

	private void updatePowerAttribute(D201ActuatorMeasurementResponse response)
	{
		// TODO Auto-generated method stub
		
	}

	private void updateEnergyAttribute(D201ActuatorMeasurementResponse response)
	{
		// TODO Auto-generated method stub
		
	}

}