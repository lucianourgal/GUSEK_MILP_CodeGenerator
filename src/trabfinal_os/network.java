/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabfinal_os;

import java.util.Random;

/**
 * @author Luciano
 */
public class network {

    boolean listarCaminhos = false;
    
    int[] nos;

    private int[] parOD1;
    private int[] parOD2;
    private int[] quantidadeCarros;

    private int[] aresta1;
    private int[] aresta2;
    int contA = 0;

    private int[][] trajeto;
    int[] tamanhoTrajeto;

    int[] caminhoAux;
    int caminhoAuxTam = 0;
    int distanciaM;
    boolean encontrou;
    Random gerador;

    public network() {
        //48 nós. ?? arestas. 14 pontos O-D (1 até 14)
        gerador = new Random();

        declaracaoArestas();
        gerarParesOD();
        gerarCaminhos();
        gerarQuantCarros();

    }

    public String STRINGsomaRotasPassamPorSensor(int ax, int bx){
    String s="(";
    
    for(int a=0;a<parOD1.length;a++)
        for(int b=0;b<tamanhoTrajeto[a]-1;b++)
            if(trajeto[a][b]==ax && trajeto[a][b+1]==bx)
                s = s+ "OD"+parOD1[a]+"to"+parOD2[a]+"+";
   
    if(s.length()<2)
        return "0";
    
    return s.substring(0, s.length()-1)+")";
    }
    
    public String STRINGcoberturaDeRotaPorSensor(int ax, int bx){
    String s="\nsubj to rCob"+ax+"_"+bx+": (";
    
    for(int a=0;a<parOD1.length;a++)
        if(parOD1[a]==ax && parOD2[a]==bx)
            for(int b=0;b<tamanhoTrajeto[a]-1;b++)
                s = s+"b" +trajeto[a][b]+"sen"+trajeto[a][b+1]+"+";
     
    return s.substring(0, s.length()-1)+")>=2;";
    }
    
    
    public int INTSomaRotasPassamPorSensor(int ax, int bx){
    int sm = 0;
    
    for(int a=0;a<parOD1.length;a++)
        for(int b=0;b<tamanhoTrajeto[a]-1;b++)
            if(trajeto[a][b]==ax && trajeto[a][b+1]==bx)
                sm += quantidadeCarros[a];
     
    return sm;
    }
    
    
    public void gerarQuantCarros(){
    
    quantidadeCarros = new int[parOD1.length];
    
    for(int a=0;a<parOD1.length;a++){
    quantidadeCarros[a] = 8 + gerador.nextInt(20);
   // System.out.println("OD"+parOD1[a]+"to"+parOD2[a]+": "+quantidadeCarros[a]);
    }
    
    }
    
    public int getQuantidadeCarros(int par){
    
    return quantidadeCarros[par];
        
    }
    
    
    public void gerarCaminhos() {
        setTrajeto(new int[getParOD1().length][100]);
        tamanhoTrajeto = new int[getParOD1().length];

        for (int a = 0; a < getParOD1().length; a++) {
            preparaBusca();
            
            while(distanciaM>20)
                searchWay(getParOD2()[a], getParOD1()[a], new int[200], 0);
                
            getTrajeto()[a] = caminhoAux;
            tamanhoTrajeto[a] = distanciaM + 2;
            if(listarCaminhos)
                printCaminho(a);
        }

        System.out.println("OK: Gerou todos os caminhos entre pontos OD!");
    }

    public void printCaminho(int a) {
        String x = getParOD1()[a] + "->" + getParOD2()[a] + ": ";
        for (int c = 0; c < tamanhoTrajeto[a]; c++) {
            //   System.out.println("a="+a+" c="+c);
            x = x + " " + getTrajeto()[a][c];

        }

        x = x + "; (" + tamanhoTrajeto[a] + ")";

        System.out.println(x);
    }

    //quer encontrar nó FIND. recebe lista de nós para procurar.
    public void searchWay(int find, int atual, int[] path, int distancia) {

        if (distancia >= distanciaM) /*||distancia>nos[atual] )*/ {
            return;
        }

        if (encontrou) {
            return;
        }

        //  System.out.println("CALL p "+find+" de "+atual+ " dist "+distancia);
        int[] path2 = new int[200];// = path;
        
        for(int z=0;z<distancia;z++)
            path2[z]=path[z];
        
        nos[atual] = distancia;
        path2[distancia] = atual;

        //vê se está na vizinhança;
        for (int a = 0; a < contA; a++) {
            if ((getAresta1()[a] == find && getAresta2()[a] == atual) 
                    || (getAresta2()[a] == find && getAresta1()[a] == atual)) {
                //se estiver, retorna caminho até aqui
                distanciaM = distancia;
                caminhoAuxTam = distancia + 2;
                path2[distancia + 1] = find;
                caminhoAux = path2;
               // encontrou = true;
                return; 
            }}
            
            int v;
            //se não estiver, indica próximos nós a buscar
            for (int ab = 0; ab < contA * 2; ab++) {
                v = (gerador.nextInt(contA));
                if (getAresta2()[v] == atual && nos[getAresta1()[v]] > distancia) {
                    searchWay(find, getAresta1()[v], path2, distancia + 1);
                } else if (getAresta1()[v] == atual && nos[getAresta2()[v]] > distancia) {
                    searchWay(find, getAresta2()[v], path2, distancia + 1);
                }

            }
        

    }

    public void preparaBusca() {
        nos = new int[49];
        for (int a = 1; a < 49; a++) {
            nos[a] = 150;
        }
        caminhoAux = new int[200];
        caminhoAuxTam = 0;
        distanciaM = 150;
        encontrou = false; // cuidado. Pode pegar o primeiro caminho que encontrar
    }

    public void na(int a, int b) {
        novaAresta(a, b);
    }

    public void novaAresta(int a, int b) {
        aresta1[contA] = a;
        aresta2[contA] = b;
        contA++;
        //aresta1[contA]=b;
        //aresta2[contA]=a;
        //contA++;
    }

    public void gerarParesOD() {

        //os de fora visitam os de dentro
        // 8 dentro, 6 fora. 6x8 = 48. Ida e volta: 96
        parOD1 = new int[96];
        parOD2 = new int[96];
        int x = 0;

        for (int a = 1; a < 9; a++) {
            for (int b = 9; b < 15; b++) {

                parOD1[x] = a;
                parOD2[x] = b;
                x++;
                parOD1[x] = b;
                parOD2[x] = a;
                x++;
            }
        }
        System.out.println("OK: " + x + " pares O-D gerados.");

    }

    public void declaracaoArestas() {

        aresta1 = new int[76];
        aresta2 = new int[76];

        //vertical h0 h1
        na(15, 9);
        na(16, 9);
        na(18, 9);
        na(20, 9);
        //horizontal 1
        na(15, 16);
        na(16, 17);
        na(17, 18);
        na(18, 19);
        na(19, 20);
        //vertical h1 h3
        na(10, 15);
        na(15, 22);
        na(1, 23);
        na(16, 21);
        na(21, 24);
        na(17, 2);
        na(2, 25);
        na(18, 26);
        na(19, 3);
        na(3, 27);
        na(20, 28);
        na(20, 14);
        //horizontal 2 e 3
        na(21, 2);
        na(10, 22);
        na(22, 23);
        na(23, 24);
        na(24, 25);
        na(25, 26);
        na(27, 28);
        na(28, 14);
        //vertical h3 h5
        na(22, 32);
        na(23, 4);
        na(4, 34);
        na(24, 29);
        na(29, 35);
        na(25, 5);
        na(5, 36);
        na(26, 30);
        na(30, 37);
        na(6, 38);
        na(28, 31);
        na(14, 31);
        na(31, 39);
        //horizontal 4 e 5
        na(4, 29);
        na(29, 5);
        na(30, 6);
        na(32, 33);
        na(33, 34);
        na(34, 35);
        na(35, 36);
        na(36, 37);
        na(37, 38);
        na(38, 39);
        //vertical h5 h7
        na(32, 11);
        na(33, 11);
        na(40, 11);
        na(35, 40);
        na(40, 43);
        na(43, 45);
        na(36, 7);
        na(7, 46);
        na(37, 41);
        na(41, 44);
        na(44, 47);
        na(38, 8);
        na(39, 42);
        na(42, 48);
        //horizontal 6 7    
        na(40, 7);
        na(41, 8);
        na(11, 45);
        na(45, 46);
        na(46, 47);
        na(47, 48);
        na(48, 13);
        //vertical end
        na(45, 12);
        na(47, 12);
        na(48, 12);

        System.out.println("OK: " + contA + " arestas criadas.");
    }

    /**
     * @return the aresta1
     */
    public int[] getAresta1() {
        return aresta1;
    }

    /**
     * @return the aresta2
     */
    public int[] getAresta2() {
        return aresta2;
    }

    /**
     * @return the trajeto
     */
    public int[][] getTrajeto() {
        return trajeto;
    }

    /**
     * @param trajeto the trajeto to set
     */
    public void setTrajeto(int[][] trajeto) {
        this.trajeto = trajeto;
    }

    /**
     * @return the parOD1
     */
    public int[] getParOD1() {
        return parOD1;
    }

    /**
     * @return the parOD2
     */
    public int[] getParOD2() {
        return parOD2;
    }

}
