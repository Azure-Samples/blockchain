using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Newtonsoft.Json;
using Newtonsoft.Json.Schema;
using System.ComponentModel.DataAnnotations;
using System.IO;
using System.IO.Compression;
using System.Web.Mvc;

namespace PatternCodeGen.CodeGen
{
    public class Template
    {
        [JsonProperty(Required = Required.Always)]
        [RegularExpression(@"^[_A-Za-z]([A-Za-z_0-9]*)$")]
        [MaxLength(50)]
        public string ItemName { get; set; }
        [JsonProperty(Required = Required.Always)]
        [RegularExpression(@"^[_A-Za-z]([A-Za-z_0-9]*)$")]
        [MaxLength(50)]
        public uint Version { get; set; }
    }

    abstract public class CodeGen 
    {
        protected string inputJSONString;
        protected string _ItemName, _ApplicationName, _RegistryContracyName, _ItemContractName;
        protected uint _Version;
        protected Template inputJSON;
        protected Type templateType;
        protected string SchemaString;
        private const string SchemaVersionUriString = "http://json-schema.org/draft-06/schema#";

        public CodeGen(string _inputJSONString, Type _templateType)
        {
            inputJSONString = _inputJSONString;
            templateType = _templateType;
            SchemaString = GenerateSchemaFromCSharpType();
        }

        private string GenerateSchemaFromCSharpType()
        {
            Newtonsoft.Json.Schema.Generation.JSchemaGenerator generator = new Newtonsoft.Json.Schema.Generation.JSchemaGenerator()
            {
                DefaultRequired = Required.DisallowNull,
            };

            JSchema schema = generator.Generate(templateType);
            schema.SchemaVersion = new Uri(SchemaVersionUriString);
            schema.Title = "Input Template";
            schema.Description = "Input Template";
            schema.Type = JSchemaType.Object;

            return schema.ToString();
        }

        abstract protected Template Deserialize(JSchemaValidatingReader validatingReader);
        abstract protected void init();
        abstract protected void JSONGen(string outputPath);
        abstract protected void SolGen(string outputPath);

        private Template ValidateAgainstSchema()
        {
            var errors = new List<string>();
            var schema = JSchema.Parse(SchemaString);
            var reader = new JsonTextReader(new StringReader(inputJSONString));
            var validatingReader = new JSchemaValidatingReader(reader);
            validatingReader.Schema = schema;
            validatingReader.ValidationEventHandler += (o, a) => errors.Add(a.Message);
            return Deserialize(validatingReader);
        }

        public ActionResult GenerateZipFile()
        {
            ActionResult result;
            try
            {
                inputJSON = ValidateAgainstSchema();
            }
            catch (Exception e)
            {
                result = new ContentResult();
                ((ContentResult)result).Content = e.Message;
                return result;
            }

            _ItemName = inputJSON.ItemName;
            _Version = inputJSON.Version;
            init();

            string outputPath = Path.GetTempPath();
            string guid = Guid.NewGuid().ToString();
            string zipFilePath = Path.Combine(outputPath, _ApplicationName + guid + ".zip");
            outputPath = Path.Combine(outputPath, "outputDirectory" + guid);
            Directory.CreateDirectory(outputPath);

            JSONGen(outputPath);
            SolGen(outputPath);

            try
            {
                ZipFile.CreateFromDirectory(outputPath, zipFilePath, CompressionLevel.Fastest, false);
            }
            catch (Exception e)
            {
                result = new ContentResult();
                ((ContentResult)result).Content = "Failed to generate Zip file";
                return result;
            }

            using (FileStream stream = File.Open(zipFilePath, FileMode.Open))
            {
                using (MemoryStream ms = new MemoryStream())
                {
                    stream.CopyTo(ms);
                    result = new FileContentResult(ms.ToArray(), "application/zip");
                    ((FileContentResult)result).FileDownloadName = inputJSON.ItemName + "RegistryContractPackage.zip";
                }
            }
                
            Directory.Delete(outputPath, true);
            File.Delete(zipFilePath);
            return result;
        }

        private string indent(int indentLevel)
        {
            return "".PadLeft(indentLevel * 4);
        }

        protected string addIndentation(string str, int indentLevel)
        {
            str = str.Replace("\r\n", "\n");
            str = str.Replace("\r", "\n");
            str = str.Replace("\n", "\n" + indent(indentLevel));
            return indent(indentLevel) + str;
        }
    }
}
