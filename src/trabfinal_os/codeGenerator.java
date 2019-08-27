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
public class codeGenerator {
    
     
    network net;
    
    public void printCodigo(network nt){
    net = nt;
    String code ="\n#GUSEK CODE BELOW:\n\n";
        
    //declara variaveis
    code = code + "#Declaracao de variaveis:\n#Binarias de sensores ativos:\n";
    code = code + declBinariasSensor();
    code = code + "\n#Veiculos por rota:\n";
    code = code + declQuantidadesRota();
    code = code + "\n#Veiculos em sensor/aresta:\n";
    code = code + declVeiculoSensor();
        
    //função objetivo
    code = code + "\n\n#Funcao objetivo:\n";
    code = code + funcaoFitnessMinTotalSensores();
    
    //declara restrições
    code = code + "\n\n#Conjunto de restricoes:";
    code = code + "\n#Min e Max de sensores ativos (Total: 154):";
    code = code + restrQuantSensor();
    code = code + "\n#Toda rota deve estar coberta por pelo menos 2 sensores:";
    code = code + coberturaODSensor();
    code = code + "\n#Cada sensor (ativo) precisa ter medição >= que o real:";
    code = code + noMinimoIgualarSensores();
    code = code + "\n#Contabilizando veículos por link a partir das rotas que passam pelo link:";
    code = code + restrCalcularQuantLink();
    code = code + "\n#Calibragem para evitar OD pairs muito grandes:";
    code = code + calibragemODPairs();
    
    //fim do código
    code = code + "\n\nsolve;";
    code = code + "\nprintf \"\\n\\n#QUALIDADE DE FLUXO NOS SENSORES:\\n\";";
    code = code + printFRelat();
    code = code + "\n\nend;\n\n";
    
    System.out.println(code);
    }
    
    public String declBinariasSensor(){
    String s = "";
    int [] aresta1 = net.getAresta1();
    int [] aresta2 = net.getAresta2();
    
    for(int a=0;a<aresta1.length;a++){
        s = s+ "var b"+aresta1[a]+"sen"+aresta2[a]+" binary; ";
        s = s+ "var b"+aresta2[a]+"sen"+aresta1[a]+" binary; ";
    }
    return s;
    }
    
    public String declQuantidadesRota(){
    String s = "";
    int [] aresta1 = net.getParOD1();
    int [] aresta2 = net.getParOD2();
    
    for(int a=0;a<aresta1.length;a++){
        s = s+ "var OD"+aresta1[a]+"to"+aresta2[a]+" >=5; ";
      
        }
    return s;
    }
    
    /*public String restrQuantidadesRota(){
    String s = "";
    int [] aresta1 = net.getParOD1();
    int [] aresta2 = net.getParOD2();
   
    for(int a=0;a<aresta1.length;a++){
        s = s+ "\nsubj to r"+aresta1[a]+"to"+aresta2[a]+": "+aresta1[a]+"to"+aresta2[a]+" >= 0; ";
      
        }
    return s;
    }*/
    
    public String restrQuantSensor(){
    String s = ""; String t = "";
    int [] aresta1 = net.getAresta1();
    int [] aresta2 = net.getAresta2();
    
    for(int a=0;a<aresta1.length;a++){
        s = s+ "b"+aresta1[a]+"sen"+aresta2[a]+"+";
        s = s+" b"+aresta2[a]+"sen"+aresta1[a]+"+";
    }
    
    s = "("+s.substring(0, s.length()-1)+")";
    
    t = "\nsubj to minSen: "+ s +" >= 50; \nsubj to maxSen: "+ s +" <= 130;";
    
    return t;
    }

    private String declVeiculoSensor() {
       String s = "";
    int [] aresta1 = net.getAresta1();
    int [] aresta2 = net.getAresta2();
    
    for(int a=0;a<aresta1.length;a++){
        s = s+ "var C"+aresta1[a]+"to"+aresta2[a]+" >=0; ";
        s = s+ "var C"+aresta2[a]+"to"+aresta1[a]+" >=0; ";
    }
    return s.substring(0, s.length()-1);
    }
    
    public String restrCalcularQuantLink(){
    String s = ""; 
    int [] aresta1 = net.getAresta1();
    int [] aresta2 = net.getAresta2();
    
   
    for(int a=0;a<aresta1.length;a++)
        s = s+ "\nsubj to Cal"+aresta1[a]+"to"+aresta2[a]+": "
                + "C"+aresta1[a]+"to"+aresta2[a]+" = "+net.STRINGsomaRotasPassamPorSensor(aresta1[a], aresta2[a])+";";
  
    
    return s;
    }

    private String coberturaODSensor() {
      
    String s = "";
    int [] a1 = net.getParOD1();
    int [] a2 = net.getParOD2();
    
    for(int a=0;a<a1.length;a++){
        s = s+  net.STRINGcoberturaDeRotaPorSensor(a1[a], a2[a]);
      
    }
    
    return s;
    }
    
    public String funcaoFitness(){
    String s = "minimize z: ";
    //sensor a sensor:   b17sen18(900 - C17to18)
    int [] a1 = net.getAresta1();
    int [] a2 = net.getAresta2();
    
    for(int a=0;a<a1.length;a++)
        s = s + "b"+a1[a]+"sen"+a2[a]+"*"
                + "(C"+a1[a]+"to"+a2[a]+" - "+net.INTSomaRotasPassamPorSensor(a1[a], a2[a])+")*b"+a1[a]+"sen"+a2[a]+" + ";
    
    return s.substring(0, s.length()-2)+";";
    }
    
    public String funcaoFitnessMinTotalSensores(){
    String s = "minimize z: ";
    //sensor a sensor:   b17sen18(900 - C17to18)
    int [] a1 = net.getAresta1();
    int [] a2 = net.getAresta2();
    
    for(int a=0;a<a1.length;a++)
        s = s + " C"+a1[a]+"to"+a2[a]+" - (b"+a1[a]+"sen"+a2[a]+"*"+net.INTSomaRotasPassamPorSensor(a1[a], a2[a])+") +";
    
    return s.substring(0, s.length()-2)+";";
    }
    
    
    public String noMinimoIgualarSensores(){
    String s = "";
    //sensor a sensor:   b17sen18(900 - C17to18)
    int [] a1 = net.getAresta1();
    int [] a2 = net.getAresta2();
    
    for(int a=0;a<a1.length;a++)
        s = s + "\nsubj to min"+a1[a]+"sen"+a2[a]+": "
                + " C"+a1[a]+"to"+a2[a]+" >= "+net.INTSomaRotasPassamPorSensor(a1[a], a2[a])+"*b"+a1[a]+"sen"+a2[a]+";";
    
    
    return s;
    }

    private String calibragemODPairs() {
        String s = "";
        int [] a1 = net.getParOD1();
    int [] a2 = net.getParOD2();
    
    for(int a=0;a<a1.length;a++){
        s = s+  "\nsubj to calib"+a1[a]+"to"+a2[a]+": OD"+a1[a]+"to"+a2[a]+" <= 30;";
      
    }
    
    return s;
        
        
    }

    private String printFRelat() {
       String s = "";
       //printf "! %3d | %3d | %3d  | %3d  |  \n", 1, 
       //(1*m1s1t1+2*m1s1t2+3*m1s1t3), m1i1, m1f1;
       int [] a1 = net.getAresta1();
       int [] a2 = net.getAresta2();
    
       for(int a=0;a<a1.length;a++)
           if(net.INTSomaRotasPassamPorSensor(a1[a], a2[a])!=0)
           s = s + "\nprintf \"!C"+a1[a]+"to"+a2[a]+": %3d / "
                   + ""+net.INTSomaRotasPassamPorSensor(a1[a], a2[a])+" = %10.2f \\n\", C"+a1[a]+"to"+a2[a]+", C"+a1[a]+"to"+a2[a]+"/"+net.INTSomaRotasPassamPorSensor(a1[a], a2[a])+";";
       else
               s = s + "\nprintf \"!C"+a1[a]+"to"+a2[a]+": 0 \\n\";";
       
         s = s + "\nprintf \"\\n\\n#QUALIDADE DE FLUXO O-D:\\n\";";
         
       
       a1 = net.getParOD1();
       a2 = net.getParOD2();  
         
       for(int a=0;a<a1.length;a++)
           s = s + "\nprintf \"!OD"+a1[a]+"to"+a2[a]+": %3d / "
                   + ""+net.getQuantidadeCarros(a)+" = %10.2f \\n\", OD"+a1[a]+"to"+a2[a]+", OD"+a1[a]+"to"+a2[a]+"/"+net.getQuantidadeCarros(a)+";";
       
         
       return s;
    }
    
    
    
}
