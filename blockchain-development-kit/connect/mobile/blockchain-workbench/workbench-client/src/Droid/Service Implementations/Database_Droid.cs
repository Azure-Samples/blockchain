using System;
using System.IO;

using Workbench.Forms.Interfaces;

namespace Workbench.Forms.Droid.ServiceImplementations
{
    public class Database_Droid : IDatabase
    {
        public string GetDbPath(string dbName)
        {
            if (!dbName.Contains(".db3"))
                dbName += ".db3";

            string path = Environment.GetFolderPath(Environment.SpecialFolder.Personal);
            return Path.Combine(path, dbName);
        }
    }
}