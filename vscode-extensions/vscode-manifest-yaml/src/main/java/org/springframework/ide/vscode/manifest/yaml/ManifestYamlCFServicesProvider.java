/*******************************************************************************
 * Copyright (c) 2017 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.vscode.manifest.yaml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Provider;

import org.springframework.ide.vscode.commons.cloudfoundry.client.CFServiceInstance;
import org.springframework.ide.vscode.commons.cloudfoundry.client.cftarget.CFClientTarget;
import org.springframework.ide.vscode.commons.yaml.schema.BasicYValueHint;
import org.springframework.ide.vscode.commons.yaml.schema.YValueHint;

public class ManifestYamlCFServicesProvider implements Provider<Collection<YValueHint>> {

	private final List<CFClientTarget> targets;

	private static final Logger logger = Logger.getLogger(ManifestYamlCFServicesProvider.class.getName());

	public ManifestYamlCFServicesProvider(List<CFClientTarget> targets) {
		this.targets = targets;
	}

	@Override
	public Collection<YValueHint> get() {
		List<YValueHint> hints = new ArrayList<>();

		if (targets != null) {
			for (CFClientTarget cfClientTarget : targets) {

				try {
					List<CFServiceInstance> services = cfClientTarget.getClientRequests().getServices();
					if (services != null) {
						for (CFServiceInstance service : services) {
							String name = service.getName();
							String label = getServiceLabel(cfClientTarget, service);
							YValueHint hint = new BasicYValueHint(name, label);
							if (!hints.contains(hint)) {
								hints.add(hint);
							}
						}
					} 
				} catch (Exception e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
		return hints;
	}

	private String getServiceLabel(CFClientTarget cfClientTarget, CFServiceInstance service) {
		return service.getName() + " - " + service.getPlan() + " (" + cfClientTarget.getParams().getOrgName() + " - "
				+ cfClientTarget.getParams().getSpaceName() + ")";
	}
}
