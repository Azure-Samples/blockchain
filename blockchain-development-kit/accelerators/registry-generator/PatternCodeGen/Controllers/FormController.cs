using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using PatternCodeGen.CodeGen;

namespace PatternCodeGen.Controllers
{
    public class FormController : Controller
    {
        // GET: Form
        public ActionResult Index()
        {
            return View();
        }

        [HttpPost]
        public ActionResult RegistryContractGenerate(string inputJSONString)
        {
            Response.ContentType = "text/plain";
            CodeGen.CodeGen codeGen = new CodeGen.RegistryContractGen(inputJSONString);
            return codeGen.GenerateZipFile();
        }
    }
}
