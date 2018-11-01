import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Stack;

class Operation {
	public Operation(String strOperator, BigDecimal bgDecimalOp1) {
		this.strOperator = strOperator;
		this.bgDecimalOp1 = bgDecimalOp1;
	}

	// for binary operator + - * /
	public Operation(String strOperator, BigDecimal bgDecimalOp1, BigDecimal bgDecimalOp2) {
		this.strOperator = strOperator;
		this.bgDecimalOp1 = bgDecimalOp1;
		this.bgDecimalOp2 = bgDecimalOp2;
	}

	// for clear
	public Operation(String operator, ArrayList<BigDecimal> list) {
		this.strOperator = operator;
		this.list = list;
	}

	public String strOperator;
	public BigDecimal bgDecimalOp1;
	public BigDecimal bgDecimalOp2;
	ArrayList<BigDecimal> list = new ArrayList<>();
}
public class calc {

	private static boolean isNumeric(String str)
	{
		try
		{
			Double.parseDouble(str);
		}
		catch(NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}

	static Stack<BigDecimal> stack_val = new Stack<>();
	static Stack<Operation> operations = new Stack<>();

	private static void processaline(String input) {
		String[] strInputVal = input.split(" ");

		// go through each value
		for (int pos = 0; pos < strInputVal.length; pos++) {
			String strValues = strInputVal[pos];
			if (isNumeric(strValues)) {
				stack_val.push(new BigDecimal(strValues));
				operations.push(new Operation("push", new BigDecimal(strValues)));
			} else { // is an operator
				if ((strValues.equals("+")) || (strValues.equals("-")) || (strValues.equals("*")) || (strValues.equals("/"))) {
					if (stack_val.size() <= 1) {
						System.out.printf("operator %s (position: %d): insufficient parameters%n", strValues, 2 * pos + 1); // suppose there's one space between each value
						break;
					} else {
						BigDecimal op2 = stack_val.pop();
						BigDecimal op1 = stack_val.pop();

						if (strValues.equals("+")) stack_val.push(op1.add(op2));
						else if (strValues.equals("-")) stack_val.push(op1.subtract(op2));
						else if (strValues.equals("*")) stack_val.push(op1.multiply(op2));
						else if (strValues.equals("/"))  {
							if (op2.equals(BigDecimal.ZERO)) {
								System.out.println("Divide by zero not allowed");
								stack_val.push(op1);
								break;
							} stack_val.push(op1.divide(op2, 2, RoundingMode.HALF_EVEN));
						}
						operations.push(new Operation(strValues, op1, op2));
					}
					//}
				} else if (strValues.equals("clear")) {

					ArrayList<BigDecimal> origdata = new ArrayList<>();
					while (!stack_val.empty()) origdata.add(0, stack_val.pop());
					operations.push(new Operation("clear", origdata));
					stack_val.clear();
				} else if (strValues.equals("sqrt")) {
					if (stack_val.empty()) {
						System.out.format("operator sqrt (position: %d): insufficient parameters%n", 2 * pos + 1);
						break;
					} else {
						BigDecimal bgDecimalOp = stack_val.pop();
						stack_val.push(BigDecimal.valueOf(StrictMath.sqrt(bgDecimalOp.doubleValue())));
						operations.push(new Operation("sqrt", bgDecimalOp));
					}
				} else if (strValues.equals("undo")) {
					Operation lastop = operations.pop();
					if (!stack_val.empty()) stack_val.pop();
					if (lastop.strOperator.equals("clear")) {
						for (BigDecimal d : lastop.list) {
							stack_val.push(d);
						}
					} else if (lastop.strOperator.equals("sqrt")) {
						stack_val.push(lastop.bgDecimalOp1);
					} else if (lastop.strOperator.equals("push")) {
					} else if (lastop.strOperator.equals("+") || (lastop.strOperator.equals("-")) || lastop.strOperator.equals("*") || lastop.strOperator.equals("/")) {
						stack_val.push(lastop.bgDecimalOp1);
						stack_val.push(lastop.bgDecimalOp2);
					}
				}
			}
		}
		System.out.print("Stack: ");
		for (BigDecimal bgDecimal : stack_val) {
			System.out.print(bgDecimal + " ");
		}
		System.out.println();
	}

	public static void main(String[] args) {
		BufferedReader buffReader = null;

		try {
			buffReader = new BufferedReader(new InputStreamReader(System.in));

			while (true) {
				String strInput = buffReader.readLine();
				processaline(strInput); // process a line here
				if ("q".equals(strInput)) {
					System.out.println("Exit!");
					System.exit(0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (buffReader != null) {
				try {
					buffReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
