package ru.fizteh.fivt.students.elenav.shell;

import java.io.IOException;

public class Shell {
	public static void main(String[] args) throws IOException {
		ShellState shell = new ShellState();
		shell.init();
		
		if (args.length == 0) {
			shell.interactive(System.out);
		} else {
			StringBuilder sb = new StringBuilder();
			for (String s : args) {
				sb.append(s);
				sb.append(" ");
			}
			String monoString = sb.toString(); 
			
			monoString = monoString.trim();
			String[] commands = monoString.split("\\s*;\\s*");
			for (String command : commands) {
				try {
					shell.execute(command, System.out);
				} catch (IOException e) {
					System.err.println(e.getMessage());
					System.exit(1);
				}
			}
		}	
	}
}