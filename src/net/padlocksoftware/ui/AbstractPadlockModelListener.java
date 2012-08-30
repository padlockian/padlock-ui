/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.padlocksoftware.ui;

public abstract class AbstractPadlockModelListener implements PadlockModelListener {

   ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

   ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

   //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

   /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

   ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
   
   //------------------------ Implements: PadlockModelListener
   
   public void keyPairAdded(String name){};

   public void keyPairRemoved(String name){};

   public void keyPairUpdated(String name){};

   public void licenseAdded(String name){};

   public void licenseRemoved(String name){};

   public void licenseUpdated(String name){};

   public void padlockStateUpdated(){};

   public void templateAdded(String name){};

   public void templateRemoved(String name){};

   public void templateUpdated(String name){};

   //------------------------ Overrides:

   //---------------------------- Abstract Methods -----------------------------

   //---------------------------- Utility Methods ------------------------------

   //---------------------------- Property Methods -----------------------------
}