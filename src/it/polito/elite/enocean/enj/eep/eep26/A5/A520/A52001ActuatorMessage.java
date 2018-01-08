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
package it.polito.elite.enocean.enj.eep.eep26.A5.A520;

/**
 * A class representing the interpeted payload of a 4BS telegram generated by an
 * occupancy sensor having the A50701 EEP profile.
 * 
 * @author bonino
 *
 */
public class A52001ActuatorMessage
{
	// the current valve position
	private int currentValue;

	// the service on flag
	private boolean serviceOn;

	// the nergy input enabled flag
	private boolean energyInputEnabled;

	// the energy storage flag
	private boolean energyStorage;

	// the battery capacity flag
	private boolean batteryCapacity;

	// the cover open flag
	private boolean coverOpen;

	// the temperature sensor failure flag
	private boolean temperatureSensorFailure;

	// the window open detection flag
	private boolean windowOpen;

	// the actuator obstructed flag
	private boolean actuatorObstructed;

	// the temperature
	private int temperature;

	// the validity flag
	private boolean valid;

	private boolean teachIn;

	/**
	 * Class constructor, given the telegram payload parses the content and sets
	 * up the internal fields to reflect the payload content in a more
	 * accessible form.
	 * 
	 * @param data
	 *            the 4BS telegram payload as an array of byte.
	 */
	public A52001ActuatorMessage(byte data[])
	{
		// initially not valid
		this.valid = false;

		// check the data lenght, shall be 4
		if (data.length == 4)
		{

			byte currentValue = data[0];
			// transform into a positive integer
			this.currentValue = 0x00FF & currentValue;

			byte serviceOnAsByte = (byte) (data[1] & (byte) 0x80);
			serviceOn = (serviceOnAsByte > 0);

			byte energyInputEnabledAsByte = (byte) (data[1] & (byte) 0x40);
			energyInputEnabled = (energyInputEnabledAsByte > 0);

			byte energyStorageAsByte = (byte) (data[1] & (byte) 0x20);
			energyStorage = (energyStorageAsByte > 0);

			byte batteryCapacityAsByte = (byte) (data[1] & (byte) 0x10);
			batteryCapacity = (batteryCapacityAsByte == 0);

			byte coverOpenAsByte = (byte) (data[1] & (byte) 0x08);
			coverOpen = (coverOpenAsByte > 0);

			byte temperatureSensorFailureAsByte = (byte) (data[1] & (byte) 0x04);
			temperatureSensorFailure = (temperatureSensorFailureAsByte > 0);

			byte windowOpenAsByte = (byte) (data[1] & (byte) 0x02);
			windowOpen = (windowOpenAsByte > 0);

			byte actuatorObstructedAsByte = (byte) (data[1] & (byte) 0x01);
			actuatorObstructed = (actuatorObstructedAsByte > 0);

			byte temperature = data[3];
			// transform into a positive integer
			this.temperature = 0x00FF & temperature;

			// decode the teach-in flag
			// get the teach-in flag (offset 28, 4th bit of the 4th byte)
			byte teachIn = (byte) ((byte) (data[3] & (byte) 0x08) >> 3);

			// check the corresponding boolean value
			if (teachIn == 0)
				this.teachIn = true;
			else
				this.teachIn = false;

			// everything fine....
			// TODO: check if it is better to check the values of instance
			// variables to assess validity of the message.
			this.valid = true;
		}
	}

	public int getCurrentValue()
	{
		return currentValue;
	}

	public boolean isServiceOn()
	{
		return serviceOn;
	}

	public boolean isEnergyInputEnabled()
	{
		return energyInputEnabled;
	}

	public boolean isEnergyStorage()
	{
		return energyStorage;
	}

	public boolean isBatteryCapacity()
	{
		return batteryCapacity;
	}

	public boolean isCoverOpen()
	{
		return coverOpen;
	}

	public boolean isTemperatureSensorFailure()
	{
		return temperatureSensorFailure;
	}

	public boolean isWindowOpen()
	{
		return windowOpen;
	}

	public boolean isActuatorObstructed()
	{
		return actuatorObstructed;
	}

	public int getTemperature()
	{
		return temperature;
	}

	public boolean isValid()
	{
		return valid;
	}

	public boolean isTeachIn()
	{
		return teachIn;
	}

}
