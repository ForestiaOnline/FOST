/*
Forestia Online Sprite Tool (FOST)
A tool to extract BMP from & create Forestia Online SPZ files.

Copyright (C) 2020 Sean Stafford (a.k.a. PyroSamurai)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
*/
import java.io.*;
import java.nio.*;
import java.nio.file.*;
import static java.lang.System.out;
/**
Class Description:
The Main class is the 'main' class of the FOST program (this is obvious).
It's essentially the interface for all functionality of the program.

Dev Notes:
The program can't currently create SPZ files, b/c I haven't worked all the
details of the file format. Even if it were to be able to, the list of sprites
seems deeply ingrained in the game itself. In addition to Sprites.Def, I have
also seen this inside the game's executable as well.

This makes me think that some of some of the sprites may have been hard-coded
into the game. If they are, you wouldn't be able to change any of the hard-coded
sprite files.

Regardless, the program's version has been placed at 0.9.0, b/c it cannot create
SPZ files yet. The format is in the docs folder, let me know if you figure out
anything that is undefined.

*/
public class Main
{
// Functions ordered by importance & thus more likely to be edited
// class variables
public static char mode;
public static int argsLen=0, fLen=0;
public static String dir, name, fs=File.separator;
public static File spzFile;

// Main function (keep clean)
public static void main(String[] args)
{
    argsLen = args.length;
    argCheck(args);
    for(int i=1; i < argsLen; i++)
    {
        spzFile = new File(args[i]);
        if(spzFile.exists()==false) argErrors(3);
        if(spzFile.isDirectory()==true) argErrors(4);
        setFileVars(spzFile);
        byte[] fosBA = file2BA(spzFile);
        switch(mode)
        {
        case 'c':
            //Create optC = new Create(fosBA,dir,name);
            //break;
        default:
            Extract optE = new Extract(fosBA,dir,name,fLen);
            break;
        }
    }
}

// Set basic file info
private static void setFileVars(File file)
{
    // Get the file name
    name = file.getName();
    // Get file extension's first char location
    int extIdx = name.lastIndexOf('.');
    // Remove the extension from the file name
    name = name.substring(0,extIdx);
    // Assign the directory where we will extract the image to
    dir = file.getParent()+fs;
    if(dir.equals("null"+fs)) dir=System.getProperty("user.dir")+fs;
    // Get the file size
    fLen = (int)file.length();
}

// Determines validity of cmd-line args & returns the resulting case number
// This exists as a function because it would make main() ugly if it didn't.
private static void argCheck(String[] args)
{
    // check for existence of mode argument of the correct length
    if(argsLen!=0 && args[0].length()==1)
    {
        // assign first argument to mode
        mode = args[0].charAt(0);
        // This 'if' tree checks for valid mode arg and correct # of args
        // for the given mode. Invokes helpful error messages on failure.
        if((mode=='e' || mode=='c'))
        {
            if(argsLen < 2) argErrors(2);
        }
        else
        {
            argErrors(1);
        }
    }
    else
    {
        argErrors(0);
    }
}

// Invalid command-line arguments responses
private static void argErrors(int argErrorNum)
{
    String errMsg0,errMsg1,errMsg2,errMsg3,errMsg4,errMsg5,errMsg6;
    errMsg0 ="Error: Mode argument is too long or missing";
    errMsg1 ="Error: Invalid Mode argument";
    errMsg2 ="Error: Incorrect number of arguments for this Mode";
    errMsg3 ="Error: File does not exist. Nice try.";
    errMsg4 ="Error: File is a Directory. Try being more specific.";
    switch(argErrorNum)
    {
    case 0:
        out.println(errMsg0);
        break;
    case 1:
        out.println(errMsg1);
        break;
    case 2:
        out.println(errMsg2);
        break;
    case 3:
        out.println(errMsg3);
        break;
    case 4:
        out.println(errMsg4);
        break;
    default:
        out.println("Unknown Error");
        break;
    }
    usage();
    System.exit(1);
}

// Standard usage output, explaining available modes & required arguments
private static void usage()
{
    String cr, use, col, bdr, ope, opc, ex;
    // You are not allowed to remove this copyright notice or its output
    cr ="Forestia Online Sprite Tool (FOST)\n"+
        "https://github.com/ForestiaOnline/FOST\n"+
        "Copyright (C) 2020 Sean Stafford (a.k.a. PyroSamurai)\n"+
        "License: GPLv3+\n\n";

    use="Usage: java -jar FOST.jar {mode} {/path/file.spz} {etc}\n";
    col="| Mode | Arguments | Description                      |\n";
    bdr="=======================================================\n";
    ope="|  e   | [file(s)] | Extract BMP from SPZ files       |\n";
    opc="|  c   | [file(s)] | (Placeholder)                    |\n";

    ex ="Example: java -jar FOST.jar e ../ex/path/all.spz\n";

    // Actual output function
    out.println("\n"+cr+use+bdr+col+bdr+ope+opc+bdr+ex);
}

// An anti-duplication + better readability function
private static byte[] file2BA(File file)
{
    byte[] ba = new byte[(int)file.length()];
    try
    {
        ba = Files.readAllBytes(file.toPath());
    }
    catch(Exception ex)
    {
        out.println("Error in (file2BA):");
        ex.printStackTrace(System.out);
    }
    return ba;
}
}
