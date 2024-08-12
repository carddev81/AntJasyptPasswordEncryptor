package com.omo.tools.ant.tortoisepatch;

import com.github.sarxos.winreg.HKey;
import com.github.sarxos.winreg.WindowsRegistry;

/**
 * This class contains the Windows 10 CVS Tortoise Patch method.
 *
 * @author Richard Salas March 06, 2019
 */
public class CvsTortoisePatch {

    /**
     * This method will run the tortoise patch which is to update the timestamp of the LastAdvert entry so that Windows 10 does not cause an Access Denied problem during any cvs executions.
     *
     * @return message the success message or error message on whether or not the cvsnt advertising was successfully turned off
     */
    public static String executeTortoisePatch(){
        //setting some variables here
        String message = null;
        WindowsRegistry reg = null;
        String registryBranch = null;
        String currentTimestamp = null;

        try{
            //trying to access the registry key that is causing the problem
            reg = WindowsRegistry.getInstance();
            registryBranch = "Software\\Cvsnt\\cvsadvert";

            //this next line will make sure that the key exists, if not then an error will be thrown.
            System.out.println(reg.readStringValues(HKey.HKCU, registryBranch));

            currentTimestamp = String.valueOf(System.currentTimeMillis());
            reg.writeStringValue(HKey.HKCU, registryBranch, "LastAdvert", currentTimestamp.substring(0, currentTimestamp.length() - 3));
            message = "CVS Tortoise Advertisement was turned off successfully";
        }catch(Exception e){
            message = "Could not reset cvs advertising timestamp.  Message returned by the patch is:  " + e.getMessage();
        }//end

        return message;
    }//end method

}//end class
