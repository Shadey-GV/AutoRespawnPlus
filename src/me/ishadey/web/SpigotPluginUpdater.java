package me.ishadey.web;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * All credit for this updater class goes to Bentipa
 * https://www.spigotmc.org/members/bentipa.48835/
 *
 * Edited by iShadey to support their plugins.
 */

public class SpigotPluginUpdater {
	
    private URL url;
    private final JavaPlugin plugin;
    private final String pluginurl;
    private boolean canceled = false;
    
    public SpigotPluginUpdater(JavaPlugin plugin, String pluginurl) {
        try {
            url = new URL("http://www.ishadey.ga/plugins/" + pluginurl + "/plugin.html");
        } catch (MalformedURLException e) {
            canceled = true;
            plugin.getLogger().log(Level.WARNING, "[{0}] An error occured while checking for updates! Invalid URL.", plugin.getName());
        }
        this.plugin = plugin;
        this.pluginurl = "http://www.ishadey.ga/plugins/" + pluginurl + "/plugin.html";
    }
    
    private String version = "";
    private String downloadURL = "";
    private String changeLog = "";
    private boolean out = true;
    
    public boolean needsUpdate() {
        if (canceled)
            return false;
        
        try {
            URLConnection con = url.openConnection();
            InputStream _in = con.getInputStream();
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(_in);
            Node nod = doc.getElementsByTagName("item").item(0);
            NodeList children = nod.getChildNodes();
            version = children.item(1).getTextContent();
            downloadURL = children.item(3).getTextContent();
            changeLog = children.item(5).getTextContent();
            if (newVersionAvailiable(plugin.getDescription().getVersion(), version.replaceAll("[a-zA-z ]", ""))) {
                if (out) {
                    plugin.getLogger().log(Level.INFO, " New Version found: {0}", version);
                    plugin.getLogger().log(Level.INFO, " Download it here: {0}", downloadURL);
                    plugin.getLogger().log(Level.INFO, " Changelog: {0}", changeLog);
                    broadcast(String.format("&8[&6%s&8]&a New Version found: %s", plugin.getDescription().getName(), version));
                    broadcast(String.format("&8[&6%s&8]&a Download it here: %s", downloadURL));
                }
                return true;
            }
        } catch (IOException | SAXException | ParserConfigurationException e) {
            plugin.getLogger().log(Level.INFO, "An error occured while updating.");
            broadcast(String.format("&8[&6%s&8] &cAn error occured while updating.", plugin.getDescription().getName()));
        }
        
        return false;
    }
    
    public boolean newVersionAvailiable(String arg0, String arg1) {
        if (arg0 != null && arg1 != null) {
        	try {
            	int oldvInt = Integer.parseInt(arg0.replaceAll("[^0-9]", ""));
            	int newvInt = Integer.parseInt(arg1.replaceAll("[^0-9]", ""));
            	return (newvInt > oldvInt);
        	} catch (Exception e) {
        	}
        }
        return false;
    }
    
    public void update() {
        try {
        	
            URL download = new URL(getFolder(pluginurl) + downloadURL);
            BufferedInputStream in = null;
            FileOutputStream fout = null;
            
            try {
            	
                if (out)
                    plugin.getLogger().log(Level.INFO, "Attempting to update...");
                in = new BufferedInputStream(download.openStream());
                fout = new FileOutputStream("plugins/" + downloadURL);

                final byte data[] = new byte[1024];
                int count;
                while ((count = in.read(data, 0, 1024)) != -1)
                    fout.write(data, 0, count);
                
            } finally {
                if (in != null)
                    in.close();
                if (fout != null)
                    fout.close();
            }
            
            if (out) {
                plugin.getLogger().log(Level.INFO, "Update was successful! Please restart to load the new version.");
                broadcast(String.format("&8[&6%s&8] &aUpdate was successful! Please restart to load the new version.", plugin.getDescription().getName()));
            }
            
        } catch (IOException e) {
            plugin.getLogger().log(Level.INFO, String.format("An error occured while updating.", plugin.getDescription().getName()));
            broadcast(String.format("&8[&6%s&8] &cAn error occured while updating.", plugin.getDescription().getName()));
        }
        
    }
    
    private String getFolder(String s) {
        return s.substring(0, s.lastIndexOf("/") + 1);
    }
    
    public void broadcast(String message) {
    	for (Player p : Bukkit.getOnlinePlayers()) {
    		if (p.isOp()) {
    			p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    		}
    	}
    }
    
}