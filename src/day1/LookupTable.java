package day1;

class LookupTable {
    String[] data = new String[256];

    LookupTable() {
        for (int i = 0; i < 256; i++) {
            data[i] = "Value_" + i;
        }
    }

    String lookup(byte key) {
        return data[key];
    }
}
