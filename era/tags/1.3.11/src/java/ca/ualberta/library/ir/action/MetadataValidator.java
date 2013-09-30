/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: MetadataValidator.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.action;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationError;
import net.sourceforge.stripes.validation.ValidationErrors;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.ISBNValidator;

import ca.ualberta.library.ir.localization.LocaleResources;
import ca.ualberta.library.ir.model.inputform.Form;
import ca.ualberta.library.ir.model.inputform.InputType;
import ca.ualberta.library.ir.model.inputform.Validate;
import ca.ualberta.library.ir.model.metadata.Field;
import ca.ualberta.library.ir.service.ServiceFacade;

/**
 * The MetadataValidator class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class MetadataValidator {
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(MetadataValidator.class);

	@SuppressWarnings("unused")
	private final ApplicationActionBeanContext context;
	@SuppressWarnings("unused")
	private final ServiceFacade services;
	private final ResourceBundle resources;
	private final ValidationErrors errors;

	public MetadataValidator(ApplicationActionBeanContext context) {
		this.context = context;
		this.services = context.getServices();
		this.resources = LocaleResources.getResourceBundle(context.getLanguage());
		this.errors = context.getValidationErrors();
	}

	public void validate(List<Field> fields, Form inputForm) throws Exception {
		Map<String, ca.ualberta.library.ir.model.inputform.Field> fieldMap = inputForm.getFieldMap();
		for (Field field : fields) {
			String key = field.getKey();
			ca.ualberta.library.ir.model.inputform.Field fld = fieldMap.get(key);
			if (fld == null || fld.getValidate() == null || fld.getInputType().getValue().equals(InputType.Value.label)) {
				continue;
			}
			validate(field, fld);
		}
	}

	/**
	 * The validate method.
	 * 
	 * @param field
	 * @param fld
	 * @param validationErrors
	 */
	private void validate(Field field, ca.ualberta.library.ir.model.inputform.Field fld) {
		Validate validate = fld.getValidate();

		// field label
		String label = fld.getLabel().getId() == null ? fld.getLabel().getValue() : resources.getString(fld.getLabel()
			.getId());

		// required
		if (BooleanUtils.isTrue(validate.getRequired())) {
			if (GenericValidator.isBlankOrNull(field.getValue())) {
				ValidationError error = new LocalizableError("metadata.required.valueNotPresent", label);
				errors.addGlobalError(error);
				errors.add(field.getFieldName(), error);
			}
		}

		// min length
		if (validate.getMinLength() != null && StringUtils.trimToNull(field.getValue()) != null) {
			if (!GenericValidator.minLength(field.getValue(), validate.getMinLength())) {
				ValidationError error = new LocalizableError("metadata.minlength.valueTooShort", label,
					validate.getMinLength());
				errors.addGlobalError(error);
				errors.add(field.getFieldName(), error);
			}
		}

		// max length
		if (validate.getMaxLength() != null && StringUtils.trimToNull(field.getValue()) != null) {
			if (!GenericValidator.maxLength(field.getValue(), validate.getMaxLength())) {
				ValidationError error = new LocalizableError("metadata.maxlength.valueTooLong", label,
					validate.getMaxLength());
				errors.addGlobalError(error);
				errors.add(field.getFieldName(), error);
			}
		}

		// min value
		if (validate.getMinValue() != null && StringUtils.trimToNull(field.getValue()) != null) {
			if (!GenericValidator.minValue(NumberUtils.toDouble(field.getValue()), validate.getMinValue())) {
				ValidationError error = new LocalizableError("metadata.minvalue.valueBelowMinimum", label,
					validate.getMinValue());
				errors.addGlobalError(error);
				errors.add(field.getFieldName(), error);
			}
		}

		// max value
		if (validate.getMaxValue() != null && StringUtils.trimToNull(field.getValue()) != null) {
			if (!GenericValidator.maxValue(NumberUtils.toDouble(field.getValue()), validate.getMaxValue())) {
				ValidationError error = new LocalizableError("metadata.maxvalue.valueAboveMaximum", label,
					validate.getMaxValue());
				errors.addGlobalError(error);
				errors.add(field.getFieldName(), error);
			}
		}

		// regular expression
		if (validate.getRegexp() != null && StringUtils.trimToNull(field.getValue()) != null) {
			if (!GenericValidator.matchRegexp(field.getValue(), validate.getRegexp())) {
				ValidationError error = new LocalizableError("metadata.regexp.valueNotMatch", label);
				errors.addGlobalError(error);
				errors.add(field.getFieldName(), error);
			}
		}

		// date format
		if (validate.getDateFormat() != null && StringUtils.trimToNull(field.getValue()) != null) {
			if (!GenericValidator.isDate(field.getValue(), validate.getDateFormat(), true)) {
				ValidationError error = new LocalizableError("metadata.datevalue.invalidFormat", label,
					validate.getDateFormat());
				errors.addGlobalError(error);
				errors.add(field.getFieldName(), error);
			}
		}

		// email
		if (BooleanUtils.isTrue(validate.getEmail()) && StringUtils.trimToNull(field.getValue()) != null) {
			if (!GenericValidator.isEmail(field.getValue())) {
				ValidationError error = new LocalizableError("metadata.email.invalidValue", label);
				errors.addGlobalError(error);
				errors.add(field.getFieldName(), error);
			}
		}

		// url
		if (BooleanUtils.isTrue(validate.getUrl()) && StringUtils.trimToNull(field.getValue()) != null) {
			if (!GenericValidator.isUrl(field.getValue())) {
				ValidationError error = new LocalizableError("metadata.url.invalidValue", label);
				errors.addGlobalError(error);
				errors.add(field.getFieldName(), error);
			}
		}

		// isbn
		if (BooleanUtils.isTrue(validate.getIsbn()) && StringUtils.trimToNull(field.getValue()) != null) {
			if (!new ISBNValidator().isValid(field.getValue())) {
				ValidationError error = new LocalizableError("metadata.isbn.invalidValue", label);
				errors.addGlobalError(error);
				errors.add(field.getFieldName(), error);
			}
		}
	}
}
