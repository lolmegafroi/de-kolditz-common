/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     Till Kolditz
 *******************************************************************************/
package de.kolditz.common.ui.rcp;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * This denotes an eclipse plugin instance that offers a method to get the image descriptor for the given image path.
 * 
 * @author <a href="mailto:Till.Kolditz@GoogleMail.com">Till Kolditz &lt;Till.Kolditz@GoogleMail.com&gt;</a>
 * @see ImagesInitializer
 * @see ImageConstant
 */
public interface IImageDescriptorServingPlugin
{
    /**
     * @param path
     *            the path to the image file
     * @return the image file's {@link ImageDescriptor}
     */
    ImageDescriptor getImageDescriptor(String path);
}
