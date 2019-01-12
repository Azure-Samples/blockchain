const Ganache = require("ganache-core");
const path = require("path");
const fs = require("fs");

let HOME = process.env["HOME"];

try {
  fs.mkdirSync(path.join(HOME, "database"));
} catch (err) {}

let options = {
  mnemonic: "sea bonus pistol meat idea peace sphere split wrist mouse evil diagram",
  network_id: 7331,
  db_path: path.join(HOME, "database"),
  logger: console,
  gasLimit: "0x2faf080" // 50,000,000 gas limit for private enterprise apps
};

const provider = Ganache.provider(options);

module.exports = async function (context, req) {
  if (req.body) {
    try {
      context.res = {
        status: 200,
        body: await send(req.body)
      };
    } catch (err) {
      if (req.body.id) {
        context.res = {
          jsonrpc: "2.0",
          id: res.body.id,
          error: err
        }
      } else {
        context.res = {
          status: 500,
          body: err
        };
      }
    }
  } else {
    context.res = {
      status: 400,
      body: "Bad request"
    };
  }
  return context
};

function send(req) {
  return new Promise(function (accept, reject) {
    provider.sendAsync(req, function (err, res) {
      if (err && !res) {
        reject(err);
      }

      accept(res);
    });
  });
}
