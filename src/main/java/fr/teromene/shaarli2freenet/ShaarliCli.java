package fr.teromene.shaarli2freenet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.todesbaum.jsite.application.Freenet7Interface;
import de.todesbaum.jsite.application.InsertListener;
import de.todesbaum.jsite.application.Node;
import de.todesbaum.jsite.application.Project;
import de.todesbaum.jsite.application.ProjectInserter;
import de.todesbaum.jsite.main.Configuration;
import de.todesbaum.jsite.main.ConfigurationLocator;
import net.pterodactylus.util.io.StreamCopier.ProgressListener;

/**
 * Command-line interface for jSite.
 *
 * @author David ‘Bombe’ Roden &lt;bombe@freenetproject.org&gt;
 */
public class ShaarliCli implements InsertListener {

	/** Object used for synchronization. */
	private Object lockObject = new Object();

	/** Writer for the console. */
	private PrintWriter outputWriter = new PrintWriter(System.out, true);

	/** The freenet interface. */
	private Freenet7Interface freenetInterface;

	/** The project inserter. */
	private ProjectInserter projectInserter = new ProjectInserter();

	/** The projects. */
	private List<ShaarliProject> projects;

	/** Whether the insert has finished. */
	private boolean finished = false;

	/** Whether the insert finished successfully. */
	private boolean success;

	/**
	 * Creates a new command-line interface.
	 *
	 * @param args
	 *            The command-line arguments
	 */
	private ShaarliCli(String[] args) {
				
		ConfigurationLocator configurationLocator = new ConfigurationLocator();

		Configuration configuration = new Configuration(configurationLocator, configurationLocator.findPreferredLocation());

		
		if(!new File(System.getProperty("user.home") + "/.s2f/").exists()) {
			
			configuration.save();
			
		}
		
		projectInserter.addInsertListener(this);
		projects = configuration.getShaarliProjects();
		Node node = configuration.getSelectedNode();

		freenetInterface = new Freenet7Interface();
		freenetInterface.setNode(node);

		projectInserter.setFreenetInterface(freenetInterface);
        projectInserter.setPriority(configuration.getPriority());

        ShaarliProject currentProject = null;
      
        ShaarliGenerator shaarliGen = null;
        

        Options shaarliOptions = new Options();

        shaarliOptions.addOption("h", "help", false, "Prints this message");
        shaarliOptions.addOption("g", "generation-only", false, "Only generate the project, don't upload it");

        shaarliOptions.addOption("s", "sync", true, "Generates the project with the supplied name and uploads it to Freenet");
        shaarliOptions.addOption("f", "force-regenerate", false, "Force the regeneration of the entire project");
        
        shaarliOptions.addOption("n", "new", false, "Creates a new project. If no other argument specified, will prompt for the options");
        shaarliOptions.addOption("t", "title", true, "Title of your new project");
        shaarliOptions.addOption("u", "url", true, "URL of the rss feed");
        shaarliOptions.addOption("T", "Theme", true, "Theme to use for freenet");
        
        shaarliOptions.addOption("S", "Set", true, "Can be used to set the theme or the URL of a project");
    
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = null;
        
        try {
			cmd = parser.parse(shaarliOptions, args);
		} catch (ParseException exception) {
			
			outputWriter.println(exception.getMessage());
			new HelpFormatter().printHelp("Shaarli2freenet", shaarliOptions);

			System.exit(0);

		}
        
        if(cmd.getOptions().length == 0 || cmd.hasOption("help")) {
        	
			new HelpFormatter().printHelp("Shaarli2freenet", shaarliOptions);
        	return;
        	
        }
        
        if(cmd.hasOption("new")) {
        	        	
	        if(!cmd.hasOption("title") && !cmd.hasOption("url")) {
	        	
	        	projects.add(ShaarliProject.createInteractiveShaarliProject(freenetInterface));
	        	
				configuration.setShaarliProjects(projects);
				configuration.save();
				outputWriter.println("Project was created successfully.");
				
				return;
				
	        } else if(cmd.hasOption("title") && cmd.hasOption("url")) {
	        	
	        	if(cmd.hasOption("Theme")) {
	        		projects.add(new ShaarliProject(cmd.getOptionValue("title"), cmd.getOptionValue("url"), "default", freenetInterface));
	        	} else {
	        		projects.add(new ShaarliProject(cmd.getOptionValue("title"), cmd.getOptionValue("url"), cmd.getOptionValue("Theme"), freenetInterface));       		
	        	}
	        	
				configuration.setShaarliProjects(projects);
				configuration.save();
				outputWriter.println("Project was created successfully.");

				return;
	        } else {
	        	
				new HelpFormatter().printHelp("Shaarli2freenet", shaarliOptions);
				return;
	        	
	        }
        }
        
        if(cmd.hasOption("Set")) {
        	
        	for (ShaarliProject project : projects) {
    			if(project.getName().equalsIgnoreCase(cmd.getOptionValue("Set"))) {
    				currentProject = project;
    				break;
    				
    			}
    		}
        	
        	if(cmd.hasOption("Theme")) {
        		
        		currentProject.setTheme(cmd.getOptionValue("Theme"));
        		
        	}
        	if(cmd.hasOption("url")) {
        		
        		currentProject.setFeedURL(cmd.getOptionValue("url"));
        		
        	}
        	
        	configuration.setShaarliProjects(projects);
    		configuration.save();

        	return;
        	
        }
        
        if(cmd.hasOption("sync")) {
        	for (ShaarliProject project : projects) {
    			if(project.getName().equalsIgnoreCase(cmd.getOptionValue("sync"))) {
    				currentProject = project;
    				break;
    				
    			}
    		}
        	
        	if(currentProject == null) {
        		outputWriter.println("Cannot find project with this name");
        		return;
        	}

        	
    		outputWriter.println("Starting the generation of project "+currentProject.getName());

    		shaarliGen = new ShaarliGenerator(currentProject);
    		
    		try {
    			
    			if(cmd.hasOption("force-regenerate")) {
        			shaarliGen.createPages(true);    				
    			} else {
        			shaarliGen.createPages(false);
    			}
    			
    		} catch (IOException e) {
    			
    			outputWriter.println("Unable to generate the project, the error is "+e.getMessage());
    			
    		}
    		
    		currentProject = shaarliGen.getModifiedProject();	
    		configuration.setShaarliProjects(projects);
    		configuration.save();

    		
    		if(cmd.hasOption("generation-only")) return;
    		
    		if (insertProject(currentProject)) {
    			outputWriter.println("Project \"" + currentProject.getName() + "\" successfully inserted.");
    		} else {
    			outputWriter.println("Project \"" + currentProject.getName() + "\" was not successfully inserted.");
    		}
    		

        }
        
		
		
		
	}


	/**
	 * Inserts the given project.
	 *
	 * @param currentProject
	 *            The project to insert
	 * @return <code>true</code> if the insert finished successfully,
	 *         <code>false</code> otherwise
	 */
	private boolean insertProject(Project currentProject) {
		if (!freenetInterface.hasNode()) {
			outputWriter.println("Node is not running!");
			return false;
		}
		projectInserter.setProject(currentProject);
		projectInserter.start(new ProgressListener() {

			@Override
			public void onProgress(long copied, long length) {
				System.out.print("Uploaded: " + copied + " / " + length + " bytes...\r");
			}
		});
		synchronized (lockObject) {
			while (!finished) {
				try {
					lockObject.wait();
				} catch (InterruptedException e) {
					/* ignore, we're in a loop. */
				}
			}
		}
		return success;
	}

	//
	// INTERFACE InsertListener
	//

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void projectInsertStarted(Project project) {
		outputWriter.println("Starting Insert of project \"" + project.getName() + "\".");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void projectUploadFinished(Project project) {
		outputWriter.println("Project \"" + project.getName() + "\" has been uploaded, starting insert...");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void projectURIGenerated(Project project, String uri) {
		outputWriter.println("URI: " + uri);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void projectInsertProgress(Project project, int succeeded, int failed, int fatal, int total, boolean finalized) {
		outputWriter.println("Progress: " + succeeded + " done, " + failed + " failed, " + fatal + " fatal, " + total + " total" + (finalized ? " (finalized)" : "") + ", " + ((succeeded + failed + fatal) * 100 / total) + "%");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void projectInsertFinished(Project project, boolean success, Throwable cause) {
		outputWriter.println("Request URI: " + project.getFinalRequestURI(0));
		finished = true;
		this.success = success;
		synchronized (lockObject) {
			lockObject.notify();
		}
	}

	//
	// MAIN
	//

	/**
	 * Creates a new command-line interface with the given arguments.
	 *
	 * @param args
	 *            The command-line arguments
	 */
	public static void main(String[] args) {
		new ShaarliCli(args);
	}

}
