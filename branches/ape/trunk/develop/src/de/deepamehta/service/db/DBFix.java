package de.deepamehta.service.db;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.service.ApplicationServiceInstance;
import de.deepamehta.service.CorporateMemory;

import java.util.Enumeration;
import java.util.Hashtable;



final class DBFix {



	// ***************
	// *** Methods ***
	// ***************



	public static void main(String[] args) {
		System.out.println("=== DeepaMehta DB-Fix ===");
		ApplicationServiceInstance instance = null;
		int invCount = 0, fixCount = 0;
		try {
			// --- create application service ---
			instance = ApplicationServiceInstance.lookup(args);
			CorporateMemory cm = instance.createCorporateMemory();	// throws DME
			//
			Hashtable topics = cm.getTopicsByProperty("<html>");
			Enumeration e = topics.keys();
			while (e.hasMoreElements()) {
				String t = (String) e.nextElement();
				int pos = t.indexOf(':');
				String propName = t.substring(pos + 1);
				BaseTopic topic = (BaseTopic) topics.get(t);
				String propValue = cm.getTopicData(topic.getID(), 1, propName);
				//
				int pos2 = propValue.toLowerCase().indexOf("</html>");
				if (pos2 == -1) {
					System.out.println("*** " + topic + ": </html> not found in \"" + propName + "\"");
					invCount++;
				} else if (propValue.length() > pos2 + 7 + 2) {
					int n = propValue.length() - pos2 - 7 - 2;
					cm.setTopicData(topic.getID(), 1, propName, propValue.substring(0, pos2 + 7));
					System.out.println(">>> " + topic + ": " + n + " crop chars in \"" + propName + "\"");
					fixCount++;
				}
			}
			//
			System.out.println("> " + cm.getTopicCount() + " topics");
			System.out.println("> " + topics.size() + " HTML properties found");
			System.out.println("> " + invCount + " invalid");
			System.out.println("> " + fixCount + " fixed");
			cm.release();
		} catch (DeepaMehtaException e) {
			System.out.println("*** " + e);
			// ### e.printStackTrace();
		}
		System.exit(0);
	}
}
