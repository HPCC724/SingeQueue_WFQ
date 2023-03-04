package ch.ethz.systems.netbench.xpt.WFQTCP;

public class Weight_Distribution {
    private float[] weights;
    private int[] multiples;
    private int weight_num;
    public Weight_Distribution(String distribution, int weight_num){
        switch (distribution) {
            case "uniform":
                this.weight_num = weight_num;
                this.weights = new float[this.weight_num];
                this.multiples = new int[this.weight_num];
                double weight_each = 1.0/(weight_num*1.0);
                for(int i=0;i<this.weight_num;i++){
                    this.weights[i] = (float) weight_each;
                    this.multiples[i] = 1;
                }
                return;
            case "linear":
                this.weight_num = weight_num;
                this.weights = new float[this.weight_num];
                this.multiples = new int[this.weight_num];
                int sum_mutiples = 0;
                for(int i=0;i<this.weight_num;i++) {
                    this.multiples[i] = i+1;
                    sum_mutiples += i+1;
                }
                float base_weight = (float) 1.0/sum_mutiples;
                for(int i=0;i<this.weight_num;i++)
                    this.weights[i] = base_weight*this.multiples[i];
            default:
                return;
        }
    }
    public float[] get_weights(){
        return this.weights;
    }
    public int[] getMultiples(){return this.multiples;}
}
