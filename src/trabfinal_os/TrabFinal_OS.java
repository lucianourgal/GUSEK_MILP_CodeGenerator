/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabfinal_os;

/**
 *
 * @author Luciano
 */
public class TrabFinal_OS {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        network net = new network();
      
        codeGenerator cod = new codeGenerator();
        cod.printCodigo(net);
        
    }
    
}
