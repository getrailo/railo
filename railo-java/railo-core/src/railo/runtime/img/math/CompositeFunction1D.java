/*
*

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package railo.runtime.img.math;

public class CompositeFunction1D implements Function1D {

	private Function1D f1, f2;
	
	public CompositeFunction1D(Function1D f1, Function1D f2) {
		this.f1 = f1;
		this.f2 = f2;
	}
	
	@Override
	public float evaluate(float v) {
		return f1.evaluate(f2.evaluate(v));
	}
}

