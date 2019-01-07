using System;
using System.IO;

using Workbench.Forms.Interfaces;

namespace Workbench.Forms.iOS.ServiceImplementations
{
    public class Database_iOS : IDatabase
    {
        public string GetDbPath(string dbName)
        {
            if (!dbName.Contains(".db3"))
                dbName += ".db3";

            string docFolder = Environment.GetFolderPath(Environment.SpecialFolder.Personal);
            string libFolder = Path.Combine(docFolder, "..", "Library", "Databases");

            if (!Directory.Exists(libFolder))
                Directory.CreateDirectory(libFolder);

            return Path.Combine(libFolder, dbName);
        }
    }
}