package com.final_project.bikepass_android.activity;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Berk on 11.05.2020
 */
public class DiffieHellmanGroupKeyExchange {
    private static int bitSize=32;

    public DiffieHellmanGroupKeyExchange(){
        createGroupKeys(2);
    }

    public DiffieHellmanGroupKeyExchange(int num){
        createGroupKeys(num);
    }

    public static void createGroupKeys(int num) {
        BigInteger primeNum=new BigInteger(bitSize,20,new Random());
        System.out.println("Prime Number(Asal sayi): "+primeNum);
        BigInteger generator=null;
        for(int i=2001;i<100000000;i++)
            if(isPrimeRoot(BigInteger.valueOf(i),primeNum)){
                generator=BigInteger.valueOf(i);
                break;
            }
        if(generator==null)
            generator=BigInteger.valueOf(0);
        System.out.println("Generator Number: "+generator);
        System.out.println();

        Random random=new Random();
        BigInteger[] secrets=new BigInteger[num];
        for(int i=0;i<num;i++){
            secrets[i]=new BigInteger(bitSize-2,random);
            System.out.println((char)('A'+i)+"'nin gizli anahtari: "+secrets[i]);
        }
        System.out.println();

        BigInteger[] publics=new BigInteger[num];
        for(int i=0;i<num;i++){
            publics[i]=generator.modPow(secrets[i],primeNum);
            System.out.println((char)('A'+i)+"'nin acik anahtari: "+publics[i]);
        }
        System.out.println();
        if(num>2) {
            BigInteger[][] shareds = new BigInteger[num - 2][num];
            for (int i = 0; i < num; i++) {
                shareds[0][(i + 1) % num] = publics[i].modPow(secrets[(i + 1) % num], primeNum);
                System.out.println("1. turda paylasilan anahtarlar: " + shareds[0][(i + 1) % num]);
            }
            System.out.println();
            for (int i = 1; i < num - 2; i++) {
                for (int j = 0; j < num; j++) {
                    shareds[i][(j + 1) % num] = shareds[i - 1][j].modPow(secrets[(j + 1) % num], primeNum);
                    System.out.println(i + 1 + ". turda paylasilan anahtarlar:" + shareds[i][(j + 1) % num]);
                }
                System.out.println();
            }

            BigInteger[] groupKeys = new BigInteger[num];
            for (int i = 0; i < num; i++) {
                groupKeys[(i + 1) % num] = shareds[num - 3][i].modPow(secrets[(i + 1) % num], primeNum);
                System.out.println("Grup icin uretilen acik anahtar: " + groupKeys[(i + 1) % num]);
            }
        }
        else {
            BigInteger[][] shareds = new BigInteger[num - 1][num];
            for (int i = 0; i < num; i++) {
                shareds[0][(i + 1) % num] = publics[i].modPow(secrets[(i + 1) % num], primeNum);
                System.out.println("1. turda paylasilan anahtarlar: " + shareds[0][(i + 1) % num]);
            }
            System.out.println();

            System.out.println("Grup icin uretilen acik anahtar: " + shareds[0][0]);
        }
    }

    public static boolean isPrime(BigInteger r){
        return miller_rabin(r);
    }

    public static List<BigInteger> primeFactors(BigInteger number){
        BigInteger i=BigInteger.valueOf(2);
        List<BigInteger> factors=new ArrayList<>();
        while(!number.equals(BigInteger.ONE)){
            while(number.mod(i).equals(BigInteger.ZERO)){
                factors.add(i);
                number=number.divide(i);
                if(isPrime(number)){
                    factors.add(number);
                    return factors;
                }
            }
            i=i.add(BigInteger.ONE);
            if(i.equals(BigInteger.valueOf(10000)))
                return factors;
        }
        System.out.println(factors);
        return factors;
    }

    public static boolean isPrimeRoot(BigInteger g,BigInteger p){
        BigInteger totient=p.subtract(BigInteger.ONE);
        List<BigInteger> factors=primeFactors(totient);
        for(int i=0;i<factors.size();i++){
            BigInteger factor=factors.get(i);
            BigInteger t=totient.divide(factor);
            if(g.modPow(t, p).equals(BigInteger.ONE))
                return false;
        }
        return true;
    }

    private static boolean miller_rabin_pass(BigInteger a,BigInteger n){
        BigInteger n_minus_one=n.subtract(BigInteger.ONE);
        BigInteger d=n_minus_one;
        int s=d.getLowestSetBit();
        d=d.shiftRight(s);
        BigInteger a_to_power=a.modPow(d, n);
        if(a_to_power.equals(BigInteger.ONE))
            return true;
        for(int i=0;i<s-1;i++){
            if(a_to_power.equals(n_minus_one))
                return true;
            a_to_power=a_to_power.multiply(a_to_power).mod(n);
        }
        if(a_to_power.equals(n_minus_one))
            return true;
        return false;
    }

    public static boolean miller_rabin(BigInteger n){
        for(int repeat=0;repeat<20;repeat++){
            BigInteger a;
            do{
                a=new BigInteger(n.bitLength(),new SecureRandom());
            }while(a.equals(BigInteger.ZERO));
            if(!miller_rabin_pass(a,n))
                return false;
        }
        return true;
    }
}