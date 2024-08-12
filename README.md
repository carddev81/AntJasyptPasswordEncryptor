# AntJasyptPasswordEncryptor
ANT task used for is used for generating encrypted passwords or decrypting passwords.

### Dependencies
1. jasypt-1.9.3.jar
 
An example of the usage of the task is below

```
<typedef name="jasyptencryptor" classname="com.omo.tools.ant.AntJasyptPasswordEncryptor" onerror="report"/>
<if>
  <typefound name="jasyptencryptor"/>
  <then>
    <jasyptencryptor property="encryptedPassword" encryptorPasswordPhrase="docsecretpassword" password="${passwordToEncrypt}" verbose="true" decrypt="false" />
  </then>
  <else>
    <echo>jasyptencryptor task cannot be used due to Class Not Found:  com.omo.tools.ant.AntJasyptPasswordEncryptor</echo>
  </else>
</if>
```

---

The example above should give you a clear understanding of how to implement this into your build.xml.  A short explanation of the above scripting is first the typedef task is called for setting up the jasyptencryptor by attempting to load the com.omo.tools.ant.AntJasyptPasswordEncryptor class. After the task has been defined then you are able to run the jasyptencryptor task.

## Parameters/Attributes
property
: the property that will hold the encrypted password value. (required)

encryptorPasswordPhrase
: the encryption password key used for encrypting the password. (required)

password
: the password that will be encrypted  (required)

decrypt
: set this to true | false for to decrypt/encrypt a password--default value is false (not required)

verbose
: set this to true | false for verbose logging  (not required)

failonerror
: set this to true | false for failing on error during the decryption process (not required/default value is true)
