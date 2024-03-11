package ru.stepup.task3;

class Fraction implements Fractionable{
    private int num;
    private int denum;

    Fraction(int num, int denum){
        this.num = num;
        this.denum = denum;
    }

    @Override
    @Cache(100)
    public double doubleValue() {
        System.out.println("invoke double value");
        return (double) num/denum;
    }

    @Override
    @Mutator
    public void setNum(int num) {
        this.num = num;
    }

    @Override
    @Mutator
    public void setDenum(int denum) {
        this.denum = denum;
    }
}