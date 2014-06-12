'use strict';

var AndroidFullScreen = 
{
	isSupported: function(successFunction, errorFunction)
	{
		cordova.exec(successFunction, errorFunction, 'FullScreenPlugin', 'isSupported', []);
	},

	isImmersiveModeSupported: function(successFunction, errorFunction)
	{
		cordova.exec(successFunction, errorFunction, 'FullScreenPlugin', 'isImmersiveModeSupported', []);
	},

	immersiveWidth: function(successFunction, errorFunction)
	{
		cordova.exec(successFunction, errorFunction, 'FullScreenPlugin', 'immersiveWidth', []);
	},

	immersiveHeight: function(successFunction, errorFunction)
	{
		cordova.exec(successFunction, errorFunction, 'FullScreenPlugin', 'immersiveHeight', []);
	},

	hideSystemUI: function(successFunction, errorFunction)
	{
		cordova.exec(successFunction, errorFunction, 'FullScreenPlugin', 'hideSystemUI', []);
	},

	showSystemUI: function(successFunction, errorFunction)
	{
		cordova.exec(successFunction, errorFunction, 'FullScreenPlugin', 'showSystemUI', []);
	},

	showUnderSystemUI: function(successFunction, errorFunction)
	{
		cordova.exec(successFunction, errorFunction, 'FullScreenPlugin', 'showUnderSystemUI', []);
	},
	
	immersiveMode: function(successFunction, errorFunction, isSticky)
	{
		cordova.exec(successFunction, errorFunction, 'FullScreenPlugin', 'immersiveMode', [isSticky !== false]);
	}
};
