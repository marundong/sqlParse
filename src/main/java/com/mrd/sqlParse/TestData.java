package com.mrd.sqlParse;

class TestData {
        int a;
        TestData1 b;
        String d;
        TestData e;

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public TestData1 getB() {
            return b;
        }

        public void setB(TestData1 b) {
            this.b = b;
        }

        public String getD() {
            return d;
        }

        public void setD(String d) {
            this.d = d;
        }

        public TestData getE() {
            return e;
        }

        public void setE(TestData e) {
            this.e = e;
        }

    public TestData(int a, String d) {
        this.a = a;
        this.d = d;
    }

    public TestData() {
    }
}
    class TestData1 {

        double c;

        public double getC() {
            return c;
        }

        public void setC(double c) {
            this.c = c;
        }

        public TestData1(double c) {
            this.c = c;
        }
    }