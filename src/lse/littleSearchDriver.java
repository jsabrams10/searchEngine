package lse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.*;

public class littleSearchDriver {
	
	public static void main(String[] args){
		
		Scanner sc = new Scanner(System.in);
		LittleSearchEngine engine = new LittleSearchEngine();
		
		while(true){
			
			System.out.println("Enter the name of the 'docs file' or hit enter to exit =>");
			String docsName = sc.nextLine();
			
			if (docsName.length() == 0) {
				
				break;
			}
			
			System.out.println("Enter the name of the 'noise-words file' or hit enter to exit =>");
			String noisesName = sc.nextLine();
			
			if (noisesName.length() == 0) {
				
				break;
			}
			
			try{
				
				engine.makeIndex(docsName, noisesName);
			}
			
			catch (Exception e){
				
				System.out.println("One of the files could not be found.");
				return;
			}
			
			System.out.println(engine.keywordsIndex.toString());
			System.out.println("Enter 'kw1' or hit enter to exit =>");
			String kw1 = sc.nextLine();
			
			if (kw1.length() == 0) {
				
				break;
			}
			
			System.out.println("Enter 'kw2' or hit enter to exit =>");
			String kw2 = sc.nextLine();
			
			if (kw2.length() == 0) {
				
				break;
			}
			
			System.out.println(engine.top5search(kw1, kw2).toString());
		}
	
		sc.close();
	}
}