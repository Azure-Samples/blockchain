using System;
using Xamarin.Forms;
using System.Security.Cryptography;
using System.Text;

namespace Workbench.Forms.Helpers
{
    public static class RandomColorHelper
    {
        /* Although this class is unncessary, it can assist in creating a random 
         * color based on the hash of a string as input 
         */ 

		static MD5 md5 = MD5.Create();
		public static Color GetRandomColor(string value)
		{
			var hash = md5.ComputeHash(Encoding.UTF8.GetBytes(value));
			var color = Color.FromRgb(hash[0], hash[1], hash[2]);

            return color;
		}
    }
}
