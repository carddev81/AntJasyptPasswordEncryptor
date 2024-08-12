package com.omo.tools.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

import com.omo.tools.ant.tortoisepatch.CvsTortoisePatch;

/**
 * This class handles the jasypt password encryption process.
 *
 * @author Richard Salas, JCCC August 08, 2014
 */
public class JasyptPasswordEncryptorTask extends Task {

    private String password;
    private String encryptorPasswordPhrase;
    private String property;
    private boolean verbose;
    private boolean decrypt;
    private boolean fail = true;

    /**
     * Called by the project to let the task initialize properly. The default implementation is a no-op.
     *
     * @Throws: BuildException - if something goes wrong with the build
     */
    @Override public void init() {
        super.init();
    }

    /**
     * Called by the project to let the task do its work. This method may be called more than once, if the task is invoked more than once. For example, if target1 and target2 both depend on target3, then running "ant target1 target2" will run all tasks in target3 twice.
     *
     * @Throws BuildException - if something goes wrong with the build
     */
    @Override public void execute() throws BuildException {
        super.execute();

        //executing the tortoise patch
        vlog(CvsTortoisePatch.executeTortoisePatch());

        initRequiredAttributes();
        if(!decrypt){
            encryptThePassword();
        }else{
            decryptThePassword();
        }//end if
    }//end method

    /**
     * This method is called by the execute method which will decrypt a password that is sent into it and set a property to return back to the ant project using the encryptorPasswordPhrase.
     */
    private void decryptThePassword() {
        vlog("Beginning the decryption phase");

        // using the standard pbe encrytor to decrypt the users password.
        StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
        decryptor.setPassword(encryptorPasswordPhrase);

        try{
            vlog("Setting property " + property + " with the decrypted value of " + password + ".");
            getProject().setProperty(property, decryptor.decrypt(password));
            vlog("Decryption process complete.");
        }catch(EncryptionOperationNotPossibleException e){
            vlog(">>>> ERROR  Could not decrypt the value supplied to this task.");
            if(fail){
                displayErrorMessage(e);
                throw new BuildException("The value " + String.valueOf(password) + " could not be decrypted, possibly due to the value not being encrypted.  Please verify that this value is indeed encrypted.  If the value is not encrypted please inform a known ant developer to find out how to encrypt your values.");
            }else{
                vlog(">>>> ERROR  The password that was sent in to be decrypted will not change and will be set to the property: " + property);
                getProject().setProperty(property, password);
            }//end if...else
        }catch(Exception e){
            vlog(">>>> ERROR  Could not decrypt the value supplied to this task.");
            //custom logic of code ran only within the isu domain due to the veracode user id and password not existing at all within this DOMAIN!!!
            if(fail){
                displayErrorMessage(e);
                throw new BuildException("The value " + String.valueOf(password) + " could not be decrypted, possibly due to the value not being encrypted.  Please verify that this value is indeed encrypted.  If the value is not encrypted please inform a known ant developer to find out how to encrypt your values.");
            }else{
                vlog(">>>> ERROR  The value that was sent in to be decrypted will not change and will be set to the property: " + property);
                getProject().setProperty(property, password);
            }//end if
        }//end try...catch
    }//end method

    /**
     * This method is used to display an error message to the user during an exception during the decyrption process.
     * @param e the exception
     */
    private void displayErrorMessage(Exception e) {
        vlog(" >>>> ERROR  +-----------------------------------------------------------------------------------------------------------------+");
        vlog(" >>>> ERROR  |                                                                                                                 |");
        vlog(" >>>> ERROR  |   J A S Y P T   D E C R Y P T O R   T A S K   C O U L D   N O T   D E C R Y P T   P A S S E D   I N   V A L U E |");
        vlog(" >>>> ERROR  |                                                                                                                 |");
        vlog(" >>>> ERROR  +-----------------------------------------------------------------------------------------------------------------+");
        vlog(" >>>> ERROR  The value attempted to be decrypted is: " + String.valueOf(password));
        vlog(" >>>> ERROR  You will need to encrypt the " + String.valueOf(password) + " value before you can run your script successfully. Please inform a know ant developer with any questions concerning the encyption process.");
        if(e != null && e instanceof EncryptionOperationNotPossibleException){
            vlog(" >>>> ERROR  Encryption Operation Not Possible Exception");
        }else{
            vlog(" >>>> ERROR  Error Message is: " + e.getMessage());
        }//end if
    }//end method

    /**
     * This method is called by the execute method which will encrypt a password that is sent into it and set a property to return back to the project.
     */
    private void encryptThePassword() {
        vlog("Beginning the encryption phase");

        // using the standard pbe string encryptor
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(encryptorPasswordPhrase);

        String encryptedPassword = encryptor.encrypt(password);
        vlog("The encryption was successful. encrypted password is : " + encryptedPassword);

        vlog("Setting property " + property + " with the encrypted password as its value.");
        getProject().setNewProperty(property, "ENC(" + encryptedPassword + ")");

        vlog("Encryption process complete.");
    }//end method

    /**
     * Checking to see if this tasks required attributes are being utilized. If not a Build Exception will be thrown alerting the builder of a possible exception.
     *
     * @throws BuildException
     *         exception thrown when trying to initialize the values of each field.
     */
    private void initRequiredAttributes() throws BuildException {
        vlog("Initializing the required Attributes... property, encryptorPasswordPhrase, and password");

        if(this.property == null){
            throw new BuildException("Error!!! You didn't specify a 'property' name for the jasyptencryptor task!!!");
        }//end if

        if(this.encryptorPasswordPhrase == null){
            throw new BuildException("Error!!! You didn't specify a 'encryptorPasswordPhrase' for encrypting the password!!!");
        }//end if

        if(this.password == null){
            throw new BuildException("Error!!! You didn't specify a 'password' to encrypt as this is the whole purpose of this task!");
        }//end if
        vlog("The required attributes have been succesfully initialized");
    }//end method

    /**
     * logs verbose messages
     *
     * @param msg
     */
    protected final void vlog(String msg) {
        if(this.verbose){
            log(msg);
        }//end if
    }//end method

    /**
     * sets property attribute
     *
     * @param property
     */
    public void setProperty(String property) {
        this.property = property;
    }//end method

    /**
     * sets verbose attribute
     *
     * @param verbose true or false value
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }//end method

    /**
     * sets encryptorPasswordPhrase attribute
     *
     * @param encryptorPasswordPhrase the phrase or combination of characters used in the encrypt/decrypt process
     */
    public void setEncryptorPasswordPhrase(String encryptorPasswordPhrase) {
        this.encryptorPasswordPhrase = encryptorPasswordPhrase;
    }//end method

    /**
     * sets password attribute
     *
     * @param password string value
     */
    public void setPassword(String password) {
        this.password = password;
    }//end method

    /**
     * sets whether to decrypt or not
     * @param decrypt true or false value
     */
    public void setDecrypt(boolean decrypt){
        this.decrypt = decrypt;
    }//end method

    /**
     * sets whether to decrypt or not
     * @param decrypt true or false value
     */
    public void setFailOnError(boolean failonerror){
        this.fail = failonerror;
    }//end method

}//end class
