import express from "express";
import pg from "pg";
import dotenv from "dotenv";
dotenv.config();
import bodyParser from "body-parser";
import fileUpload from "express-fileupload";
import { performFunction } from "./tableSettings/db.js";
import jwt from "jsonwebtoken";
const app = express();
const conString = process.env.DATABASE_URL;
const poolConfig = {
  connectionString: conString,
  max: 20,
  idleTimeoutMillis: 70000,
  connectionTimeoutMillis: 2000,
};
export const db = new pg.Pool(poolConfig);

app.use(express.json());
app.use(
  fileUpload({
    useTempFiles: true,
  })
);

app.use(bodyParser.urlencoded({ limit: "40mb", extended: true }));
app.use(bodyParser.json({ limit: "40mb", extended: true }));
app.get("/getData", async (req, res) => {
  res.json({
    data: "code",
  });
});
app.post("/register", async (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) {
    res.status(400).json({
      data: "Please enter all details",
    });
  }
  const query1 = "select * from teachers where email = $1";
  const param1 = [email];
  const isPresent = await performFunction(query1, param1);
  //console.log(isPresent)
  if (isPresent.length > 0) {
    return res.status(400).json({
      data: "User found",
    });
  }
  console.log(email, password);
  const text = "insert into teachers (email,password) values ($1,$2)";
  const params = [email, password];

  try {
    const result = await performFunction(text, params);

    //console.log(result);
    res.status(200).json({
      data: "Registration Success",
    });
  } catch (error) {
    res.status(500).json({
      data: error,
    });
  }
});
app.post("/login", async (req, res) => {
  const { email, password } = req.body;
  console.log(email, password);
  if (!email || !password) {
    res.status(400).json({
      data: "Please enter all details",
    });
  }
  const query1 = "select * from teachers where email = $1";
  const param1 = [email];
  try {
    const isPresent = await performFunction(query1, param1);
    console.log(isPresent);
    if (isPresent.length === 0) {
      return res.status(400).json({
        data: "No User Found",
      });
    }
    if (isPresent[0].password !== password) {
      return res.status(400).json({
        data: "Password mismatch",
      });
    }
    const token = jwt.sign(
      {
        userID: isPresent[0].id,
      },
      process.env.secret_key
    );

    res.status(200).json({
      data: "Success",
      email: email,
      token: token,
    });
  } catch (error) {
    res.status(500).json({
      data: error,
    });
  }
});
const verifyToken = async (req, res, next) => {
  const { token: authorization } = req.headers;
  const temp = authorization.startsWith("Bearer");
  //console.log(temp)
  if (temp) {
    let token = authorization.split(" ")[1];
    //console.log(token)
    let { userID } = jwt.verify(token, process.env.secret_key);
    if (userID) {
      let query = "select * from teachers where id = $1";
      let params = [userID];
      const isPresent = await performFunction(query, params);
      if (isPresent.length > 0) {
        //console.log(isPresent)
        req.userID = isPresent[0].id;
        next();
      } else {
        return res.status(400).json({
          data: "User not found",
        });
      }
    } else {
      return res.status(400).json({
        data: "Verification failed",
      });
    }
  } else {
    return res.status(400).json({
      data: "Issue with token",
    });
  }
};
app.post("/candidate/add", verifyToken, async (req, res) => {
  //get loggerId from token
  
  const loggerId = req.userID;
  //console.log(loggerId,req.body)
  const { firstname, lastname, phone, roll } = req.body;
  if (firstname === "" || lastname === "" || phone === "" || roll === "") {
    return res.status(400).json({
      data:"Please fill all details"
    });
  }
  const upperFirst = firstname[0].toUpperCase() + firstname.slice(1);
  const upperLast = lastname[0].toUpperCase() + lastname.slice(1);
  let query =
    "select * from candidates where loggerid = $1 and roll = $2 and firstname = $3 and lastname = $4";
  let params = [loggerId, roll, upperFirst, upperLast];

  try {
    const isPresent = await performFunction(query, params);
    if (isPresent.length > 0) {
      return res.status(400).json({
        data: "Student Found",
      });
    }
    query =
      "insert into candidates (loggerid,firstname,lastname,phone,roll) values ($1,$2,$3,$4,$5)";
    params = [loggerId, upperFirst, upperLast, phone, roll];
    const Newres = await performFunction(query, params);
    //console.log("hi");
    return res.status(201).json({
      data: "Success"
    });
   
  } catch (error) {
    return res.status(500).json({
      data: error
    });
  }
});
app.get("/candidate/find",verifyToken,async(req,res)=>{

  const query ="select * from candidates where loggerid = $1"
  
  const params = [req.userID]
  try {
    const newP = await performFunction(query,params)
    //console.log(newP)
    return res.status(200).json({
      data:'Success',
      result:newP
    })
  } catch (error) {
    return res.status(500).json({
      data:error
    })
  }
})

app.listen(9000, () => {
  console.log("App listening on 9000");
});
