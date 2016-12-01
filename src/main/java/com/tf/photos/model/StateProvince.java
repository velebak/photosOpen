/*
 *
 * Copyright 2013-2016 Pacific Coast Professional Services, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tf.photos.model;

/**
 * Enum that contains possible states or provinces associated with supported countries.
 * @author Heather Stevens on 1/4/14.
 */
public enum StateProvince {

	ALABAMA("state.Alabama"),
	ALASKA("state.Alaska"),
	AMERICAN_SAMOA("state.AmericanSamoa"),
	ARIZONA("state.Arizona"),
	ARKANSAS("state.Arkansas"),
	CALIFORNIA("state.California"),
	COLORADO("state.Colorado"),
	CONNECTICUT("state.Connecticut"),
	DELAWARE("state.Delaware"),
	DISTRICT_OF_COLUMBIA("state.DistrictOfColumbia"),
	FLORIDA("state.Florida"),
	GEORGIA("state.Georgia"),
	GUAM("state.Guam"),
	HAWAII("state.Hawaii"),
	IDAHO("state.Idaho"),
	ILLINOIS("state.Illinois"),
	INDIANA("state.Indiana"),
	IOWA("state.Iowa"),
	KANSAS("state.Kansas"),
	KENTUCKY("state.Kentucky"),
	LOUISIANA("state.Louisiana"),
	MAINE("state.Maine"),
	MARYLAND("state.Maryland"),
	MASSACHUSETTS("state.Massachusetts"),
	MICHIGAN("state.Michigan"),
	MINNESOTA("state.Minnesota"),
	MISSISSIPPI("state.Mississippi"),
	MISSOURI("state.Missouri"),
	MONTANA("state.Montana"),
	NEBRASKA("state.Nebraska"),
	NEVADA("state.Nevada"),
	NEW_HAMPSHIRE("state.NewHampshire"),
	NEW_JERSEY("state.NewJersey"),
	NEW_MEXICO("state.NewMexico"),
	NEW_YORK("state.NewYork"),
	NORTH_CAROLINA("state.NorthCarolina"),
	NORTH_DAKOTA("state.NorthDakota"),
	NORTHERN_MARIANAS_ISLANDS("state.NorthernMarianasIslands"),
	OHIO("state.Ohio"),
	OKLAHOMA("state.Oklahoma"),
	OREGON("state.Oregon"),
	PENNSYLVANIA("state.Pennsylvania"),
	PUERTO_RICO("state.PuertoRico"),
	RHODE_ISLAND("state.RhodeIsland"),
	SOUTH_CAROLINA("state.SouthCarolina"),
	SOUTH_DAKOTA("state.SouthDakota"),
	TENNESSEE("state.Tennessee"),
	TEXAS("state.Texas"),
	UTAH("state.Utah"),
	VERMONT("state.Vermont"),
	VIRGINIA("state.Virginia"),
	VIRGIN_ISLANDS("state.VirginIslands"),
	WASHINGTON("state.Washington"),
	WEST_VIRGINIA("state.WestVirginia"),
	WISCONSIN("state.Wisconsin"),
	WYOMING("state.Wyoming"),
	
	AGUASCALIENTES("state.Aguascalientes", Country.MEXICO),
	BAJA_CALIFORNIA("state.BajaCalifornia", Country.MEXICO),
	BAJA_CALIFORNIA_SUR("state.BajaCaliforniaSur", Country.MEXICO),
	CAMPECHE("state.Campeche", Country.MEXICO),
	CHIAPAS("state.Chiapas", Country.MEXICO),
	CHIHUAHUA("state.Chihuahua", Country.MEXICO),
	COAHUILA("state.Coahuila", Country.MEXICO),
	COLIMA("state.Colima", Country.MEXICO),
	DISTRITO_FEDERAL("state.DistritoFederal", Country.MEXICO),
	DURANGO("state.Durango", Country.MEXICO),
	GUANAJUATO("state.Guanajuato", Country.MEXICO),
	GUERRERO("state.Guerrero", Country.MEXICO),
	HIDALGO("state.Hidalgo", Country.MEXICO),
	JALISCO("state.Jalisco", Country.MEXICO),
	MEXICO("state.Mexico", Country.MEXICO),
	MICHOACAN("state.Michoacan", Country.MEXICO),
	MORELOS("state.Morelos", Country.MEXICO),
	NAYARIT("state.Nayarit", Country.MEXICO),
	NUEVO_LEON("state.NuevoLeon", Country.MEXICO),
	OAXACA("state.Oaxaca", Country.MEXICO),
	PUEBLA("state.Puebla", Country.MEXICO),
	QUERETARO("state.Queretaro", Country.MEXICO),
	QUINTANA_ROO("state.QuintanaRoo", Country.MEXICO),
	SAN_LUIS_POTOSI("state.SanLuisPotosi", Country.MEXICO),
	SINALOA("state.Sinaloa", Country.MEXICO),
	SONORA("state.Sonora", Country.MEXICO),
	TABASCO("state.Tabasco", Country.MEXICO),
	TAMAULIPAS("state.Tamaulipas", Country.MEXICO),
	TLAXCALA("state.Tlaxcala", Country.MEXICO),
	VERACRUZ("state.Veracruz", Country.MEXICO),
	YUCATAN("state.Yucatan", Country.MEXICO),
	ZACATECAS("state.Zacatecas", Country.MEXICO),

	ALBERTA("province.Alberta", Country.CANADA),
	BRITISH_COLUMBIA ("province.BritishColumbia", Country.CANADA),
	MANITOBA ("province.Manitoba", Country.CANADA),
	NEW_BRUNSWICK("province.NewBrunswick", Country.CANADA),
	NEWFOUNDLAND_AND_LABRADOR("province.NewfoundlandAndLabrador", Country.CANADA),
	NORTHWEST_TERRITORIES("province.NorthwestTerritories", Country.CANADA),
	NOVA_SCOTIA("province.NovaScotia", Country.CANADA),
	NUNAVUT("province.Nunavut", Country.CANADA),
	ONTARIO("province.Ontario", Country.CANADA),
	PRINCE_EDWARD_ISLAND ("province.PrinceEdwardIsland", Country.CANADA),
	QUEBEC ("province.Quebec", Country.CANADA),
	SASKATCHEWAN ("province.Saskatchewan", Country.CANADA),
	YUKON("province.Yukon", Country.CANADA),


	ENGLAND("region.England", Country.UNITED_KINGDOM),
	SCOTLAND("region.Scotland", Country.UNITED_KINGDOM),
	WALES("region.Wales", Country.UNITED_KINGDOM),
	NORTHERN_IRELAND("region.NorthernIreland", Country.UNITED_KINGDOM);

	private String label;
	private Country country;

	private StateProvince(String label) {
		this.label = label;
		this.country = Country.UNITED_STATES;
	}

	private StateProvince(String label, Country country) {
		this.label = label;
		this.country = country;
	}

	public String getLabel() {
		return label;
	}

	public Country getCountry()	{
		return country;
	}
}
