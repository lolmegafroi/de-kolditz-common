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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import de.kolditz.common.ui.rcp.ImageConstant.RefType;
import de.kolditz.common.util.SystemProperties;

/**
 * Utility class for initializing an {@link Image} serving class with {@link ImageConstant} annotated public static
 * final {@link String}s.
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public final class ImagesInitializer
{
    private static final Logger logger = Logger.getLogger(ImagesInitializer.class);

    /**
     * Utility method for initializing an {@link Image} serving class with {@link ImageConstant} annotated public static
     * final {@link String}s.
     * 
     * @param clazz
     *            the {@link Class} to be initialized
     * @return an {@link ImageRegistry}
     * @see ImageConstant
     * @see IImageDescriptorServingPlugin
     */
    public static ImageRegistry init(Class<?> clazz, IImageDescriptorServingPlugin plugin)
    {
        ImageRegistry imageRegistry = new ImageRegistry();
        try
        {
            int mod;
            String path;
            ImageConstant refType;
            String constant;
            for(Field f : clazz.getFields())
            {
                mod = f.getModifiers();
                if(Modifier.isStatic(mod) && Modifier.isPublic(mod) && Modifier.isFinal(mod))
                {
                    try
                    {
                        constant = String.valueOf(f.get(null));
                    }
                    catch(Throwable exception)
                    {
                        logger.log(Level.ERROR, "Could not access image constant \"" + clazz.getName() //$NON-NLS-1$
                                + ImageConstant.SEPERATOR + f.getName() + '"');
                        continue;
                    }
                    refType = f.getAnnotation(ImageConstant.class);
                    // ONLY accept ref-typed fields!
                    if(refType == null)
                    {
                        logger.log(Level.WARN, "No ImageRefType annotation for image constant \"" + clazz.getName() //$NON-NLS-1$
                                + ImageConstant.SEPERATOR + f.getName() + "\" set! Found:"); //$NON-NLS-1$
                        for(Annotation a : f.getAnnotations())
                        {
                            System.out.println(a.toString());
                        }
                        continue;
                    }
                    try
                    {
                        if(refType.value() == RefType.LOCAL)
                        {
                            path = refType.folder() + SystemProperties.FILE_SEP + constant;
                        }
                        else if(refType.value() == RefType.PLATFORM_PLUGIN)
                        {
                            if(refType.remotePlugin().equals("")){ //$NON-NLS-1$
                                logger.log(Level.ERROR,
                                        "remotePlugin parameter is not set for ImageRefType annotation for image constant \"" //$NON-NLS-1$
                                                + clazz.getName() + ImageConstant.SEPERATOR + f.getName() + '"');
                                continue;
                            }
                            else
                            {
                                path = ImageConstant.PLATFORM_PLUGIN_PATH + refType.remotePlugin()
                                        + ImageConstant.SEPERATOR + refType.folder() + ImageConstant.SEPERATOR
                                        + constant;
                            }
                        }
                        else
                        {
                            logger.log(Level.ERROR, "Unknown ImageRefType annotation for image constant \"" //$NON-NLS-1$
                                    + clazz.getName() + ImageConstant.SEPERATOR + f.getName() + '"');
                            continue;
                        }
                        try
                        {
                            imageRegistry.put(constant, plugin.getImageDescriptor(path).createImage());
                        }
                        catch(RuntimeException e)
                        {
                            logger.log(Level.ERROR, constant + '=' + path, e);
                        }
                    }
                    catch(Throwable e)
                    {
                        logger.log(Level.ERROR, e.getClass().getSimpleName(), e);
                    }
                }
            }
        }
        catch(Throwable exception)
        {
            exception.printStackTrace();
        }
        return imageRegistry;
    }
}
