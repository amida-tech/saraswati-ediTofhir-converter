/* warren created on 2/23/21 inside the package - com.amida.saraswati.edifhir.service.impl */
package com.amida.saraswati.edifhir.service.impl;

import com.amida.saraswati.edifhir.exception.InvalidDataException;
import com.amida.saraswati.edifhir.exception.X12ToFhirException;
import com.amida.saraswati.edifhir.model.fhir.Fhir834;
import com.amida.saraswati.edifhir.model.fhir.Fhir835;
import com.amida.saraswati.edifhir.model.fhir.Fhir837;
import com.amida.saraswati.edifhir.service.X12ToFhirService;
import com.amida.saraswati.edifhir.service.mapper.X837Mapper;
import com.imsweb.x12.reader.X12Reader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Implements X12 file data to fhir bundle convertion using x12-parser.
 *
 * @author Warren Lin
 */
@Service
@Slf4j
public class X12ToFhirServiceImpl implements X12ToFhirService {

    @Autowired
    private X837Mapper x837Mapper;

    @Override
    public Fhir834 get834FhirBundle(File x834file) throws X12ToFhirException {
        return null;  // TODO: to be implemented.
    }

    @Override
    public Fhir835 get835FhirBundle(File x835file) throws X12ToFhirException {
        return null;  // TODO: to be implemented.
    }

    @Override
    public List<Fhir837> get837FhirBundles(File x837file)
            throws X12ToFhirException, InvalidDataException {
        try {
            X12Reader x12Reader = new X12Reader(X12Reader.FileType.ANSI837_5010_X222, x837file);
            return x837Mapper.getFhirBundles(x12Reader);
        } catch (IOException e) {
            log.error("Failed to create X12Reader. for 837", e);
            throw new X12ToFhirException();
        }
    }

    @Override
    public List<Fhir837> get837FhirBundles(X12Reader x12Reader)
            throws X12ToFhirException, InvalidDataException {
        return x837Mapper.getFhirBundles(x12Reader);
    }

    @Override
    public List<Fhir837> get837FhirBundles(String x837) throws X12ToFhirException, InvalidDataException {
        X12Reader reader;
        try {
            reader = new X12Reader(X12Reader.FileType.ANSI837_5010_X222,
                    new ByteArrayInputStream(x837.getBytes()));
            log.error("Invalid EDI X12 837 data {}. {}",
                    reader.getErrors().size(), reader.getErrors().get(0));
            reader.getErrors().forEach(log::error);
            if (!reader.getFatalErrors().isEmpty()) {
                throw new InvalidDataException(reader.getFatalErrors().get(0));
            }
            return get837FhirBundles(reader);
        } catch (IOException e) {
            log.error("Failed to read the given x12-837 data.", e);
            throw new X12ToFhirException("X12Reader error", e);
        }
    }
}
