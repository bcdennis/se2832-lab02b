package edu.msoe.se2832.taxRateCalculator.wschilling;

// Note: The following code specifically has been modified for code coverage analysis.

/**
 * @author schilling This class will calculate a users tax bill based upon their
 *         gross income and their status. It assumes no dependents and the user
 *         only uses the standard deduction (i.e. no itemization.) It is based
 *         upon the 2008 tax tables.
 */
public class TaxCalculator implements TaxCalculatorInterface {
	private String name; // This holds the name of the tax payer.
	private int filingStatus; // This holds the filing status of the tax payer.
	private int age; // This is the age of the tax payer.
	private int spouseAge; // This is the age of the spouse.
	private double grossIncome; // This is the gross income for the taxpayer.

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param name
	 *            This is the name of the taxpayer. It must be a non-empty
	 *            string.
	 * @param filingStatus
	 *            This is the filing status for the person. Single, Head of
	 *            Household, and Qualifying widower may use this constructor.
	 * @param age
	 *            This is the age of the taxpayer. Must be greater than 0 years.
	 * @throws Exception
	 *             An exception will be thrown if any parameter is out of bounds
	 *             or the filing status is incorrect (i.e. the status requires a
	 *             spouse.) Exception will also be thrown if there is not a
	 *             first and last name provided.
	 */
	public TaxCalculator(String name, int filingStatus, int age)
			throws Exception {
		// Check the validity of all parameters.
		if (name.length() <= 0) {
			throw new Exception(
					"Name must be longer than 0 characters in length. ");
		}

		// Check to make certain that a first and last name is provided.
		if (name.split("\\s+").length < 2) {
			throw new Exception(
					"Name must have at least a first and last name.");
		}

		// Check that the filing status is valid for a person who does not have
		// a spouse.
		if ((filingStatus != SINGLE) && (filingStatus != HEAD_OF_HOUSEHOLD)
				&& (filingStatus != QUALIFYING_WIDOWER)) {
			throw new Exception("Invalid filing status for this constructor.");
		}

		// Check that the age is valid.
		if (age <= 0) {
			throw new Exception("Invalid age.");
		}

		// Set the appropriate attributes of the class.
		this.name = name;
		this.filingStatus = filingStatus;
		this.age = age;
	}

	/**
	 * @param name
	 *            This is the name of the taxpayer. It must be a non-empty
	 *            string.
	 * @param filingStatus
	 *            This is the filing status for the person. Single, Head of
	 *            Household, and Qualifying widower may not use this
	 *            constructor.
	 * @param age
	 *            This is the age of the taxpayer. Must be greater than 0 years.
	 * @param spouseAge
	 *            This is the age of the spouse. Must be greater than 0 years
	 *            old.
	 * @throws Exception
	 *             An exception will be thrown if any parameter is out of bounds
	 *             or the filing status is incorrect (i.e. the status does not
	 *             require a spouse.) Exception will also be thrown if there is
	 *             not a first and last name provided.
	 */
	public TaxCalculator(String name, int filingStatus, int age, int spouseAge)
			throws Exception {
		// Check the validity of all parameters.
		if (name.length() < 0) {
			throw new Exception(
					"Name must be longer than 0 characters in length. ");
		}

		// Check to make certain that a first and last name is provided.
		if (name.split("\\s+").length < 2) {
			throw new Exception(
					"Name must have at least a first and last name.");
		}

		// Check to make certain that the status is correct.
		if ((filingStatus != MARRIED_FILING_JOINTLY)
				&& (filingStatus != MARRIED_FILING_SEPARATELY)) {
			throw new Exception("Invalid filing status for this constructor.");
		}

		// Check that the age is valid.
		if (age <= 0) {
			throw new Exception("Invalid age.");
		}

		// Check that the spouse age is valid.
		if (spouseAge <= 0) {
			throw new Exception("Invalid Age.");
		}

		// Set the attributes.
		this.name = name;
		this.filingStatus = filingStatus;
		this.age = age;
		this.spouseAge = spouseAge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see TaxCalculatorInterface#getStandardDeduction()
	 */
	public double getStandardDeduction() {
		double baseStandardDeduction = 0;
		// Based on the filing status, select the right value with the case
		// statement.
		switch (this.filingStatus) {
		case SINGLE:
			baseStandardDeduction = 5450;
			break;
		case MARRIED_FILING_JOINTLY:
			baseStandardDeduction = 10900;
			// If the spouse if greater than or equal to 65, add this amount as
			// an extra
			// deduction.
			if (this.spouseAge >= 65) {
				baseStandardDeduction += 1050;
			}
			break;
		case QUALIFYING_WIDOWER:
			baseStandardDeduction = 10900;
			break;
		case MARRIED_FILING_SEPARATELY:
			baseStandardDeduction = 5450;
			break;
		case HEAD_OF_HOUSEHOLD:
			baseStandardDeduction = 8000;
			break;
		default:
			break;
		}

		// If the filer is older than 65, the standard deduction increases as
		// well.
		if (age >= 65) {
			baseStandardDeduction += 1050;
		}
		return baseStandardDeduction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see TaxCalculatorInterface#isReturnRequired()
	 */
	public boolean isReturnRequired() {
		double under65Threshold[] = { 8950, 11500, 17900, 3500, 14400 };
		double over65Threshold[] = { 10300, 12850, 20000, 3500, 15450 };
		double currentThreshold;

		if (this.filingStatus < 0) {
			// An error has occurred.
		} else {

			if (age < 65) {
				currentThreshold = under65Threshold[this.filingStatus];
			} else {
				currentThreshold = over65Threshold[this.filingStatus];
			}
			// Adjust for married filing jointly exceptions. Essentially, the
			// rate
			// will vary based on whether a given spouse is age 65 or older. */
			if (this.filingStatus == TaxCalculatorInterface.MARRIED_FILING_JOINTLY) {
				if ((age >= 65) && (spouseAge >= 65)) {
					currentThreshold = 20000;
				} else if (((age < 65) && (spouseAge >= 65))
						|| ((age >= 65) && (spouseAge < 65))) {
					currentThreshold = 18950;
				} else {
					currentThreshold = 17900;
				}
			}
			if (this.grossIncome >= currentThreshold) {
				return true;
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see TaxCalculatorInterface#getFilingStatus()
	 */
	public int getFilingStatus() {
		return filingStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see TaxCalculatorInterface#getAge()
	 */
	public int getAge() {
		return age;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see TaxCalculatorInterface#getSpouseAge()
	 */
	public int getSpouseAge() {
		return spouseAge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see TaxCalculatorInterface#getGrossIncome()
	 */
	public double getGrossIncome() {
		return grossIncome;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see TaxCalculatorInterface#getTaxableIncome()
	 */
	public double getTaxableIncome() {
		double retVal;
		retVal = this.grossIncome - this.getStandardDeduction();
		if (retVal <= 0.0) {
			retVal = 0.0;
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see TaxCalculatorInterface#setGrossIncome(double)
	 */
	public void setGrossIncome(double grossIncome) {
		if (grossIncome < 0) {
			// Do nothing.
		} else {
			this.grossIncome = grossIncome;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see TaxCalculatorInterface#getTaxDue()
	 */
	public double getTaxDue() {
		double taxTable[] = { .1, .15, .25, .28, .33, .35, 0, 8025, 32550,
				78850, 164550, 357700, 0, 11450, 43650, 112650, 182400, 357700,
				0, 16050, 65100, 131450, 200300, 357700, 0, 8025, 32550, 65725,
				100150, 178850, 0, 16050, 65100, 131450, 200300, 357700 };
		int startingOffset = 6 * (1 + this.filingStatus);
		int index = 5;
		double remainingTaxableSalary = this.getTaxableIncome();
		double totalTax = 0.00;

		if (this.filingStatus >= 0) {

			while (index >= 0) {
				if ((startingOffset + index >= taxTable.length)
						|| (startingOffset + index < 0)) {
					// An error happened. We're out of bounds.
				} else if (remainingTaxableSalary > taxTable[startingOffset
						+ index]) {
					totalTax += (remainingTaxableSalary - taxTable[startingOffset
							+ index])
							* taxTable[index];
					remainingTaxableSalary = taxTable[startingOffset + index];
				}
				index--;
			}
		} else {
			// There is an error. Something went wrong.

		}

		return totalTax;
	}

	@Override
	public double getNetTaxRate() {
		return 100.0 * this.getTaxDue() / this.getGrossIncome();
	}
}
